package com.musicroom.resources.filters;

import javax.ws.rs.ext.Provider;

import org.json.JSONObject;

import com.musicroom.utils.UserType;

@Provider
@RequireLoggedStudio
public class IsLoggedStudioFilter extends IsLoggedFilter {
	private static final String NOT_LOGGED_AS_STUDIO = "{\"error\":\"Access denied! please login as studio first.\"}";
	
	@Override
	public boolean isAuthorized(JSONObject loggedUser) {
		
		return super.isAuthorized(loggedUser) && 
			   loggedUser.getInt("user_type_id") == UserType.STUDIO.toInt();
	}
	
	@Override
	public String getErrorObject()
	{
		return NOT_LOGGED_AS_STUDIO;
	}
}
