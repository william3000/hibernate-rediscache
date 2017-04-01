package com.xl.core.utils;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class CommonUtils {
	/** 
	 * @param obj 
	 * @return 
	 */
	public static boolean isNullOrEmpty(Object obj) {
		if (obj == null)
			return true;

		if (obj instanceof CharSequence)
			return ((CharSequence) obj).length() == 0;

		if (obj instanceof Collection)
			return ((Collection) obj).isEmpty();

		if (obj instanceof Map)
			return ((Map) obj).isEmpty();

		if (obj instanceof Object[]) {
			Object[] object = (Object[]) obj;
			if (object.length == 0) {
				return true;
			}
			boolean empty = true;
			for (int i = 0; i < object.length; i++) {
				if (!isNullOrEmpty(object[i])) {
					empty = false;
					break;
				}
			}
			return empty;
		}
		return false;
	}

	public static boolean isInEnum(Class clz, String value) {

		boolean isInEnum = false;
		for (Object enumObj : clz.getEnumConstants()) {
			if (enumObj.toString().equals(value)) {
				isInEnum = true;
				break;
			}
		}

		return isInEnum;
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String getDomin(HttpServletRequest request) {
		// StringBuffer url = request.getRequestURL();
		// String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
		String tempContextUrl = request.getHeader("referer");
		// System.out.println("getDomin referer");
		// System.out.println(tempContextUrl);
		if (CommonUtils.isNullOrEmpty(tempContextUrl)) {
			StringBuffer url = request.getRequestURL();
			tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
		} else {
			int fromIndex = tempContextUrl.indexOf("//");
			int index = tempContextUrl.indexOf("/", fromIndex + 2);
			if (index > 0) {
				tempContextUrl = tempContextUrl.substring(0, index);
			}
		}
		// System.out.println("getDomin");
		// System.out.println(tempContextUrl);
		// String ip = request.getRemoteHost();
		// String tempContextUrl = "http://" + ip;
		// System.out.println("getDomin");
		// System.out.println(tempContextUrl);
		String domin = tempContextUrl;
		String[] strArray = tempContextUrl.split(":");
		String lastStr = strArray[strArray.length - 1];
		int portLength = 0;
		if (!CommonUtils.isNullOrEmpty(lastStr)) {
			if (CommonUtils.isNumeric(lastStr)) {
				portLength = lastStr.length();
			}
		}
		if (portLength > 0) {
			domin = tempContextUrl.substring(0, tempContextUrl.length() - portLength - 1);
		}
		return domin;
	}

	public static String formatLikeParameter(String parameter) {
		return String.format("%%%s%%", parameter);
	}
	
	public static String getParaNameByClassName(String simpleName){
		Integer firstLowChar = null;
		for( int i=0;i<simpleName.length();i++){
			char c = simpleName.charAt(i);
			if(Character.isLowerCase(c)){
				firstLowChar = i;
				break;
			}
		}
		if( firstLowChar == null ){
			firstLowChar = simpleName.length()-1;
		}
		String result = simpleName.substring(0, firstLowChar).toLowerCase() + simpleName.substring(firstLowChar, simpleName.length());
		return result;
	}
	
}
