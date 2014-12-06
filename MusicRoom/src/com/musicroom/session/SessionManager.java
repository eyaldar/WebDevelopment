package com.musicroom.session;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class SessionManager {

	static final String USER = "user";

	public static JSONObject getLoggedInUser(HttpServletRequest request) 
	{
		Object sessionAttribute = request.getSession().getAttribute(USER);
		return sessionAttribute != null ? (JSONObject)sessionAttribute : null;
	}

	public static void setLoggedInUser(HttpServletRequest request, JSONObject userJSON) 
	{
		request.getSession().setAttribute(USER, userJSON);
	}
	
	public static Boolean validateUser(HttpServletRequest request, int UserId)
	{
		JSONObject loggedUser = getLoggedInUser(request);
		return loggedUser != null && loggedUser.getInt("ID") == UserId;
	}
}
