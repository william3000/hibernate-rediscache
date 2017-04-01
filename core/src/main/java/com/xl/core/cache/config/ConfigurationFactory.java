package com.xl.core.cache.config;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import net.sf.ehcache.util.ClassLoaderUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheException;

import com.xl.core.cache.redis.common.CacheManagerFactory;

public class ConfigurationFactory {
	
	private final static Log logger = LogFactory.getLog(ConfigurationFactory.class);	
	
	private static final String CONFIG_NAME = "redis.xml";
	
	private static Configuration configuration;
	
	public static Configuration getConfiguration(){
		if( configuration!=null ){
			return configuration;
		}else{
			return null;
		}
	}
	
	
	public static void initConfiguration() throws CacheException{
		try{
			URL url = ConfigurationFactory.getConfiguraitonURL();
			InputStream input = url.openStream();
			SAXPara sax = new SAXPara();
			List<ConfigItem> configs = sax.getConfig(input);
			for( ConfigItem config : configs ){
				logger.info(config.getName()+"-"+config.getMaxElementsInMemory()+"-"+config.getTimeToIdleSeconds());
			}
			configuration = new Configuration(configs);
		}catch(Exception e){
			e.printStackTrace();
			throw new CacheException(e.getMessage());
		}
	}
	
	private static URL getConfiguraitonURL() throws CacheException{
		URL url = null;
		ClassLoader standardClassloader = ClassLoaderUtil.getStandardClassLoader();
		if( standardClassloader!=null ){
			url = standardClassloader.getResource(CONFIG_NAME);
		}else{
			url = ConfigurationFactory.class.getResource(CONFIG_NAME);
		}
		if( url==null ){
			throw new CacheException("can not find config file in classpath :" + CONFIG_NAME);
		}
		return url;
	}

}
