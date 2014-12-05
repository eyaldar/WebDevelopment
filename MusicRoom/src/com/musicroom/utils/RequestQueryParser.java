package com.musicroom.utils;

import java.util.HashMap;
import java.util.Map;

public class RequestQueryParser {
	public static Map<String, String> getQueryMap(String query)  
	{  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params) {  
	    	String[] paramaterKeyValue = param.split("=");
	    	if(paramaterKeyValue.length == 2) {
		        String name = param.split("=")[0].toLowerCase();  
		        String value = param.split("=")[1];  
		        map.put(name, value); 
	    	}
	    }  
	    return map;  
	}  
}
