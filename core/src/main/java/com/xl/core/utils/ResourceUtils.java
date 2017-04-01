package com.xl.core.utils;

import java.net.URL;
import java.util.Properties;

import net.sf.ehcache.util.ClassLoaderUtil;

import org.hibernate.cache.CacheException;

import com.xl.core.cache.config.ConfigurationFactory;

public class ResourceUtils {

	public static URL getResourceURL(String path) throws Exception{
		URL url = null;
		ClassLoader standardClassloader = ClassLoaderUtil.getStandardClassLoader();
		if( standardClassloader!=null ){
			url = standardClassloader.getResource(path);
		}else{
			url = ConfigurationFactory.class.getResource(path);
		}
		if( url==null ){
			throw new CacheException("can not find config file in classpath :" + url);
		}
		return url;
	}
	
	
	public static void main(String[] args) {
		
	}
	
}
