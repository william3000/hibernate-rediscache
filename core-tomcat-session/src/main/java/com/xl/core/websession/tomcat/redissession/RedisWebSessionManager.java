package com.xl.core.websession.tomcat.redissession;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.Valve;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnectionPool;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.xl.core.websession.SessionOptions.SessionPersistPolicy;

public class RedisWebSessionManager extends ManagerBase implements Lifecycle {

	private final Log log = LogFactory.getLog(RedisWebSessionManager.class);
	
	private static String hostName;
	
	protected static String NULL_SESSION = "null";
	
	protected static boolean initFlag = false;
	
	protected static RedisClient redisClient;
	
	private static RedisConnectionPool<RedisCommands<String, String>> pool;

	protected RedisWebSessionValve valve;
	
	protected String host = "localhost";
	
	protected int port = 6379;
	
	protected int database = 0;
	
	protected String password = null;
	
	protected int timeout = 60;
	
	protected String sentinelMaster = null;
	
	protected Set<String> sentinelSet = null;
	
	
	protected ThreadLocal<RedisWebSessionLocal> currentLocal = new ThreadLocal<RedisWebSessionLocal>();
	
//	protected ThreadLocal<RedisWebSession> currentSession = new ThreadLocal<RedisWebSession>();
//	
//	protected ThreadLocal<String> currentSessionId = new ThreadLocal<String>();
//	
//	protected ThreadLocal<Boolean> currentSessionIsPersisted = new ThreadLocal<Boolean>();
	
//	protected ThreadLocal<StatefulRedisConnection<String,String>> currentConnection = new ThreadLocal<StatefulRedisConnection<String,String>>();
	
	protected ThreadLocal<RedisCommands<String, String>> currentConnetcion = new ThreadLocal<RedisCommands<String, String>>();
	
	protected static String name = "RedisSessionManager";
	
	protected EnumSet<SessionPersistPolicy> policySet = EnumSet.of(SessionPersistPolicy.DEFAULT);
	
	protected LifecycleSupport lifeCycle = new LifecycleSupport(this);
	
	//Init and Close
	public void initIdentifier(){
		try{
			hostName = InetAddress.getLocalHost().getHostName();
		}catch(Exception e){
			String mxBeanName = ManagementFactory.getRuntimeMXBean().getName();
			if( mxBeanName!=null && !mxBeanName.isEmpty() ){
				hostName = mxBeanName.split("@")[1];
			}
		}
	}
	
	public void initRedis(){
		if( !initFlag ){
			String redisMaster = "";
			if( this.getHost()==null || this.getHost().isEmpty() || this.getPort()==0 ){
				redisMaster =  "127.0.0.1:6379";
			}else{
				redisMaster = this.getHost() + ":" + this.getPort();
			}
			String redisUri = "redis://" + redisMaster;
			redisClient = RedisClient.create(redisUri);
			redisClient.setDefaultTimeout(60, TimeUnit.SECONDS);
			pool = redisClient.pool();
		}
	}
	
	public void closeRedis(){
		pool.close();
		redisClient.shutdown();
		redisClient = null;
	}
	
	public void initConnection(){
		RedisCommands<String, String> connection = pool.allocateConnection();
		log.info("initConnection:"+ (connection==null) );
		this.currentConnetcion.set(connection);
		
//		StatefulRedisConnection<String,String> connection = redisClient.connect();
//		this.currentConnection.set(connection);
	}
	
	public void closeConnection(){
		RedisCommands<String, String> connection = this.currentConnetcion.get();
		if( connection!=null ){
			pool.freeConnection(connection);
		}
		this.currentConnetcion.remove();
		
//		StatefulRedisConnection<String,String> connection = this.currentConnection.get();
//		
//		if( connection != null && connection.isOpen() ){
//			connection.close();
//		}
//		this.currentConnection.remove();
	}
	
	public void testConnection(){
		RedisCommands<String,String> command = this.getCommand();
		try{
			command.set("Test Redis", "Test Redis");
		}catch(RuntimeException e){
			e.printStackTrace();
		}
	}
	
	//ManagerBase override method
	
	public void load() throws ClassNotFoundException, IOException {
		
	}

	public void unload() throws IOException {
		
	}
	
	@Override
	protected synchronized void startInternal() throws LifecycleException {
		super.startInternal();
		
		this.setState(LifecycleState.STARTING);
		
		boolean attachedToValve = false;
		for( Valve valve : this.getContext().getPipeline().getValves() ){
			if( valve instanceof RedisWebSessionValve ){
				this.valve = (RedisWebSessionValve)valve;
				this.valve.setManager(this);
				attachedToValve = true;
				break;
			}
		}
		if( !attachedToValve ){
			String error = "Unable to attach to session handling valve; sessions cannot be saved after the request without the valve starting properly.";
			log.fatal(error);
			throw new LifecycleException(error);
		}
		
		this.setDistributable(true);
		
		this.initIdentifier();
		
		this.initRedis();
		this.initConnection();
		this.testConnection();
	}

	@Override
	protected synchronized void stopInternal() throws LifecycleException {
		if (log.isDebugEnabled()) {
			log.debug("Stopping");
		}
		
		this.setState(LifecycleState.STOPPING);
		
		this.closeConnection();
		
		super.stopInternal();
	}
	
	@Override
	public void add(Session session) {
		try{
			if( session != null ){
				log.info("add session:"+session.getId());
				this.saveSession(session, false);
			}else{
				log.info("addsave--");
				this.createSession("");
			}
		}catch(IOException ex){
			log.warn("Unable to add to session manager store: " + ex.getMessage());
		      throw new RuntimeException("Unable to add to session manager store.", ex);
		}
	}

	@Override
	public Session createSession(String requestSessionId) {
		log.info("1:"+requestSessionId);
		RedisWebSession session = null;
		String sessionId = null;
		String jvmRoute = this.getJvmRoute() == null ? hostName : this.getJvmRoute();
		RedisCommands<String,String> command = this.getCommand();
		if( null != requestSessionId ){
			log.info("2:"+requestSessionId);
			sessionId = this.generateSessionIdWithJvmRoute(requestSessionId, jvmRoute);
			//setnx : false means existed , true means set successfully
			if( !command.setnx(sessionId, NULL_SESSION)  ){
				//SessionId exists
				sessionId = null;
			}
		}else{
			//Ensure generate a unique session identifier
			log.info("3:"+requestSessionId);
			do{
				sessionId = this.generateSessionIdWithJvmRoute(this.generateSessionId(), jvmRoute);
			}while( !command.setnx(sessionId, NULL_SESSION) );
		}
		log.info("sessionId:"+sessionId);
		if( null != sessionId ){
			session = (RedisWebSession)this.createEmptySession();
			session.setNew(true);
			session.setValid(true);
			session.setCreationTime(System.currentTimeMillis());
			session.setMaxInactiveInterval(getMaxInactiveInterval());
			session.setId(sessionId);
			session.tellNew();
		}
		if( null != session ){
			try{
				RedisWebSessionLocal local = new RedisWebSessionLocal();
				local.setSession(session);
				local.setSessionIsPersisted(false);
				this.currentLocal.set(local);
				
				log.info("create session:"+session.getId());
				this.saveSession(session, true);
			}catch(IOException ex){
				log.error("Error saving newly created session: " + ex.getMessage());
				this.currentLocal.remove();
				session = null;
			}
		}
		return session;
	}

	@Override
	public Session createEmptySession() {
		return new RedisWebSession(this);
	}

	@Override
	public Session findSession(String id) throws IOException {
		log.info("findsession:"+id);
		RedisWebSession session = null;
		if( null == id ){
			this.currentLocal.remove();
		}else if( this.currentLocal.get()!=null && id.equals(this.currentLocal.get().getSessionId())){
			session = this.currentLocal.get().getSession();
		}else{
			RedisWebSessionLocal local = this.loadSession(id);
			if( local != null ){
				return local.getSession();
			}else{
				this.currentLocal.remove();
			}
		}
		return session;
	}

	@Override
	public void remove(Session session) {
		this.remove(session, false);
	}

	@Override
	public void remove(Session session, boolean update) {
		try{
			this.delSession(session.getId());
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void processExpires() {
		
	}
	
	@Override
	public int getRejectedSessions() {
		// Do nothing
		return 0;
	}
	
	public void setRejectedSessions(int i) {
		// Do nothing
	}
	
	//LifeCycle override method

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifeCycle.addLifecycleListener(listener);
	}
	
	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return lifeCycle.findLifecycleListeners();
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		lifeCycle.removeLifecycleListener(listener);
	}
	
	
	//Primary business method
	
	private String generateSessionIdWithJvmRoute(String sessionId,String jvmRoute){
//		jvmRoute = "";
		log.info("sessionId:"+sessionId+",jvmRoute="+jvmRoute);
		sessionId = "Session" + sessionId;
		if( jvmRoute!=null && !jvmRoute.isEmpty() ){
			String jvmRoutePrefix = "@" + jvmRoute;
			sessionId =  (sessionId.endsWith(jvmRoutePrefix) ? sessionId : sessionId + jvmRoutePrefix);
		}
		log.info("return sessionId:"+sessionId);
		return sessionId;
	}

	//Do after the reqeust has been done
	public void afterRequest(){
		RedisWebSession session = this.currentLocal.get()!=null ? this.currentLocal.get().getSession() : null;
		if( session != null ){
			try{
				if( session.isValid() ){
					log.info("after request:"+session.getId());
					this.saveSession(session, this.getSaveOnRequest());
				}else{
					this.remove(session);
				}
			}catch(Exception e){
				log.error("Error storing/removing session", e);
			}finally{
				this.currentLocal.remove();
				log.trace("Session removed from ThreadLocal :" + session.getIdInternal());
			}
		}
		RedisCommands<String, String> connection = this.currentConnetcion.get();
		if( connection != null ){
			pool.freeConnection(connection);
		}
		this.currentConnetcion.remove();
	}
	
	
	//Redis Operation
	
	public RedisCommands<String,String> getCommand(){
		if( pool == null ){
			this.initRedis();
		}
		RedisCommands<String,String> connection = this.currentConnetcion.get();
		if( connection == null ){
			this.initConnection();
		}
		log.info("connection:"+ (connection==null) );
		return this.currentConnetcion.get();
		
//		StatefulRedisConnection<String,String> connection = this.currentConnection.get();
//		if( connection == null || !connection.isOpen() ){
//			return redisClient.connect().sync();
//		}else{
//			return connection.sync();
//		}
	}
	
	public boolean saveSession(Session session,boolean forcePersist) throws IOException{
		Boolean isSuccess = false;
		try{
			RedisCommands<String,String> command = this.getCommand();
			RedisWebSession redisSession = (RedisWebSession)session;
			Boolean isPersisted = this.currentLocal.get()!=null ? this.currentLocal.get().isSessionIsPersisted() : null;
			byte[] orginalSessionAttribute = null;
			if( this.currentLocal.get() != null && this.currentLocal.get().getAttribute()!=null && this.currentLocal.get().getAttribute().getAttributeHash()!=null ){
				orginalSessionAttribute = this.currentLocal.get().getAttribute().getAttributeHash();
			}
			byte[] sessionAttribute = RedisWebSessionPersister.getAttributeByte(redisSession).getAttributeHash();
			if( forcePersist || redisSession.isDirty() || isPersisted==null || !isPersisted || !Arrays.equals(orginalSessionAttribute, sessionAttribute)){
				
				RedisWebSessionAttribute newAttribute = new RedisWebSessionAttribute();
				newAttribute.setAttributeHash(sessionAttribute);
				
				RedisWebSessionLocal local = new RedisWebSessionLocal();
				local.setAttribute(newAttribute);
				local.setSession(redisSession);
				isSuccess = RedisWebSessionPersister.sessionPersist(command, local, false);
				
				if( isSuccess ){
					redisSession.resetDirtyTracking();
					RedisWebSessionLocal currentLocal = this.currentLocal.get();
					currentLocal.setSessionIsPersisted(true);
					currentLocal.setAttribute(newAttribute);
					this.currentLocal.set(currentLocal);
				}
			}
			return isSuccess;
		}catch(IOException ex){
			log.warn("Unable to add to session manager store: " + ex.getMessage());
		    throw new RuntimeException("Unable to add to session manager store.", ex);
		}
	}
	
	public RedisWebSessionLocal loadSession(String id) throws IOException{
		try{
			RedisCommands<String,String> command = this.getCommand();
			RedisWebSessionLocal local = RedisWebSessionPersister.sessionLoad(this,command, id);
			if( local != null ){
				local.setSessionIsPersisted(true);
				this.currentLocal.set(local);
				return local;
			}else{
				return null;
			}
			
		}catch(IOException ex){
			this.currentLocal.remove();
			throw ex;
		}
	}
	
	public void delSession(String id) throws IOException{
		RedisCommands<String,String> command = this.getCommand();
		RedisWebSessionPersister.sessionDelete(command, id);
	}
	
	// Get and Set method for Attribute

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	
	public String getSessionPersistPolicies() {
		StringBuilder policies = new StringBuilder();
		for (Iterator<SessionPersistPolicy> iter = this.policySet.iterator(); iter.hasNext();) {
			SessionPersistPolicy policy = iter.next();
			policies.append(policy.name());
			if (iter.hasNext()) {
				policies.append(",");
			}
		}
		return policies.toString();
	}
	
	public void setSessionPersistPolicies(String sessionPersistPolicies){
		String[] policyArray = sessionPersistPolicies.split(",");
		 EnumSet<SessionPersistPolicy> policySet = EnumSet.of(SessionPersistPolicy.DEFAULT);
		 for( String policyName : policyArray ){
			 SessionPersistPolicy policy = SessionPersistPolicy.forName(policyName);
			 policySet.add(policy);
		 }
		 this.policySet = policySet;
	}
	
	public boolean getSaveOnChange(){
		return this.policySet.contains(SessionPersistPolicy.SAVE_ON_CHANGE);
	}
	
	public boolean getSaveOnRequest(){
		return this.policySet.contains(SessionPersistPolicy.SAVE_ON_REQUEST);
	}
	
	public String getSentinels() {
		StringBuilder sentinels = new StringBuilder();
		for (Iterator<String> iter = this.sentinelSet.iterator(); iter.hasNext();) {
			sentinels.append(iter.next());
			if (iter.hasNext()) {
				sentinels.append(",");
			}
		}
		return sentinels.toString();
	}
	
	public void setSentinels(String sentinels){
		if( sentinels == null ){
			sentinels = "";
		}
		String[] sentinelArray = sentinels.split(",");
		this.sentinelSet = new HashSet<String>(Arrays.asList(sentinelArray));
	}

	public Set<String> getSentinelSet() {
		return this.sentinelSet;
	}

	public String getSentinelMaster() {
		return this.sentinelMaster;
	}

	public void setSentinelMaster(String master) {
		this.sentinelMaster = master;
	}
	
}
