package com.musicroom.session;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.musicroom.database.RedisManager;

public class SessionManager {

	static final String USERID = "USERID";

	public static JSONObject getLoggedInUser(HttpServletRequest request) 
	{
		Integer id = (Integer)request.getSession().getAttribute(USERID);
		
		// if there is a logged user id
		if (id != null)
		{
			// Get user from redis
			Jedis redisConn = RedisManager.getConnection();
			String userJson = redisConn.get(USERID+id);
			RedisManager.returnResource(redisConn);
			return new JSONObject(userJson);
		}
		else
		{
			return null;
		}
	}

	public static void setLoggedInUser(HttpServletRequest request, JSONObject userJSON) 
	{
		Integer id = userJSON.getInt("id");
		
		// set json in redis
		Jedis redisConn = RedisManager.getConnection();
		redisConn.set(USERID+id, userJSON.toString());
		RedisManager.returnResource(redisConn);
		
		// set key in session
		request.getSession().setAttribute(USERID,  id);
	}
	
	public static Boolean validateUser(HttpServletRequest request, Integer UserId)
	{
		return (Integer)request.getSession().getAttribute(USERID) == UserId;
	}
	
	public static void logoutUser(HttpServletRequest request) 
	{
		Integer id = (Integer)request.getSession().getAttribute(USERID);
		
		// if there is a logged user id
		if (id != null)
		{
			// Remove from redis and from session
			Jedis redisConn = RedisManager.getConnection();
			redisConn.del(USERID+id);
			RedisManager.returnResource(redisConn);
			
			request.getSession().removeAttribute(USERID);
		}
	}
	
	public static boolean updateLoggedUserPassword(HttpServletRequest request, String password) 
	{
		JSONObject userObj = getLoggedInUser(request);

		// check if user has found
		if (userObj == null)
		{
			// return fail
			return false;
		}
		else
		{
			// update and return success
			userObj.put("password", password);
			setLoggedInUser(request, userObj);
			return true;
		}
	}
}
