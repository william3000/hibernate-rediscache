package com.xl.core.cache.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXPara extends DefaultHandler {
	
	private List<ConfigItem> configs = null;
	
	private ConfigItem config = null;
	
	private String preTag = null;
	
	public List<ConfigItem> getConfig(InputStream xmlStream) throws Exception{
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();  
        SAXPara handler = new SAXPara();  
        parser.parse(xmlStream, handler);  
        return handler.getConfig();  
	}
	
	public List<ConfigItem> getConfig(){
		return configs;
	}

	@Override
	public void startDocument() throws org.xml.sax.SAXException {
		configs = new ArrayList<ConfigItem>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws org.xml.sax.SAXException {
		if( "cache".equals(qName) ){
			config = new ConfigItem();
			String name = attributes.getValue("name");
			String maxElementsInMemory = attributes.getValue("maxElementsInMemory");
			boolean eternal = "true".equals(attributes.getValue("eternal"));
			String timeToIdleSeconds = attributes.getValue("timeToIdleSeconds");
			String timeToLiveSeconds = attributes.getValue("timeToLiveSeconds");
			config.setEternal(eternal);
			config.setMaxElementsInMemory(maxElementsInMemory);
			config.setName(name);
			config.setTimeToIdleSeconds(timeToIdleSeconds);
			config.setTimeToLiveSeconds(timeToLiveSeconds);
		}else if( "defaultCache".equals(qName) ){
			config = new ConfigItem();
			String maxElementsInMemory = attributes.getValue("maxElementsInMemory");
			boolean eternal = "true".equals(attributes.getValue("eternal"));
			String timeToIdleSeconds = attributes.getValue("timeToIdleSeconds");
			String timeToLiveSeconds = attributes.getValue("timeToLiveSeconds");
			config.setEternal(eternal);
			config.setMaxElementsInMemory(maxElementsInMemory);
			config.setName("default");
			config.setTimeToIdleSeconds(timeToIdleSeconds);
			config.setTimeToLiveSeconds(timeToLiveSeconds);
		}
		preTag = qName;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws org.xml.sax.SAXException {
		if( "cache".equals(qName) ){
			configs.add(config);
			config = null;
		}else if( "defaultCache".equals(qName) ){
			configs.add(config);
			config = null;
		}
		preTag = null;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
		super.characters(ch, start, length);
	}
	
	public static void main(String[] args) {
		
	}
	
	
	
	
	

}
