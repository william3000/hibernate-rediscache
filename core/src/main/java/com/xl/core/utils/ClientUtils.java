package com.xl.core.utils;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClientUtils {

	private final static Log logger = LogFactory.getLog("ClientUtils");

	public static String convertToJson1(boolean success, List data, String code, String message) {
		return convertToJson(success, data, code, message, "");
	}
	
	public static String convertToJson(Object obj) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDataProcess());
		String result = JSONArray.fromObject(obj, jsonConfig).toString();
		return result;
	}
	
	public static String convertToJson(boolean success, List data, String code, String message, String callback) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDataProcess());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", success);
		map.put("message", message);
		map.put("code", code);
		// map.put("callback", callback);
		map.put("data", data);
		String result = JSONObject.fromObject(map, jsonConfig).toString();
		if (callback != null && !callback.isEmpty()) {
			result = callback + "(" + result + ")";
		}
		return result;
	}

	// 重载方法
	public static String convertToJson(Map data, boolean success, String code, String message, String callback) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDataProcess());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", success);
		map.put("message", message);
		map.put("code", code);
		// map.put("callback", callback);
		map.put("data", data);
		String result = JSONObject.fromObject(map, jsonConfig).toString();
		if (callback != null && !callback.isEmpty()) {
			result = callback + "(" + result + ")";
		}
		return result;
	}
	
	public static void convertToJson4Interceptor(HttpServletRequest request, HttpServletResponse response, boolean success, String code, String message) throws Exception {
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		String domin = CommonUtils.getDomin(request);
		response.setHeader("Access-Control-Allow-Origin", domin);
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		response.setHeader("P3P", "CP=CAO PSA OUR");

		PrintWriter out = response.getWriter();
		String retrunString = ClientUtils.convertToJson(success, null, code, message, request.getParameter("callback"));
		out.print(retrunString);
	}

	public static String getRequestIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	public static Date convertDate(String dataString) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(dataString);
			return date;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String dateFormat(Date date, String formatStr) {
		String str = null;
		if (null != date) {
			DateFormat dd = new SimpleDateFormat(formatStr);
			str = dd.format(date);
		}
		return str;
	}

	public static String dateFormat(Date date) {
		return dateFormat(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static void main(String[] args) {
//		String url1 = "http://test.haolinworld.com/index.php?route=account/unitaryregister&telephone={phoneNumber}&password={password}&email=&fax=&firstname=";
//
//		url1 = url1.replaceFirst("\\{phoneNumber}", "123");
//		url1 = url1.replaceFirst("\\{password}", "456");
//
//		System.out.println(url1);
	}

}
