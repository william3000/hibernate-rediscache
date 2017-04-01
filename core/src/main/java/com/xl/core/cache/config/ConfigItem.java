package com.xl.core.cache.config;

public class ConfigItem {

	private int id;
	
	private String name;
	
	private String maxElementsInMemory;
	
	private boolean eternal;
	
	private String timeToIdleSeconds;
	
	private String timeToLiveSeconds;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMaxElementsInMemory() {
		return maxElementsInMemory;
	}

	public void setMaxElementsInMemory(String maxElementsInMemory) {
		this.maxElementsInMemory = maxElementsInMemory;
	}

	public boolean isEternal() {
		return eternal;
	}

	public void setEternal(boolean eternal) {
		this.eternal = eternal;
	}

	public String getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public void setTimeToIdleSeconds(String timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	public String getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(String timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}
	
	
	
}
