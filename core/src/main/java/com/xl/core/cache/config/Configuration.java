package com.xl.core.cache.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
	
	private static final String DEFAULT_STRING = "default";
	
	private static Map<String,ConfigItem> configMap = new HashMap<String,ConfigItem>();
	
	public Configuration( List<ConfigItem> configs ){
		if( configs!=null && !configs.isEmpty() ){
			for(ConfigItem configItem : configs){
				configMap.put(configItem.getName(), configItem);
			}
		}
	}
	
	public static ConfigItem getConfig(String name){
		ConfigItem item = configMap.get(name);
		if( item!=null ){
			return item;
		}else{
			return configMap.get(DEFAULT_STRING);
		}
	}
	
	
	public static ConfigItem getRealConfig(String name){
		return configMap.get(name);
	}
	
	public static ConfigItem getDefaultConfig(){
		return configMap.get(DEFAULT_STRING);
	}

}
