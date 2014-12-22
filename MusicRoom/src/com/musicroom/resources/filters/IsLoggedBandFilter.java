package com.musicroom.resources.filters;

import javax.ws.rs.ext.Provider;

import org.json.JSONObject;

import com.musicroom.utils.UserType;

@Provider
@RequireLoggedBand
public class IsLoggedBandFilter extends IsLoggedFilter {
	private static final String NOT_LOGGED_AS_BAND = "{\"error\":\"Access denied! please login as a band first.\"}";
	
	@Override
	public boolean isAuthorized(JSONObject loggedUser) {
		
		return super.isAuthorized(loggedUser) && 
			   loggedUser.getInt("USER_TYPE_ID") == UserType.BAND.toInt();
	}
	
	@Override
	public String getErrorObject()
	{
		return NOT_LOGGED_AS_BAND;
	}
}
