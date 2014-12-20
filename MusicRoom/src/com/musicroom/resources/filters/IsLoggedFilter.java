package com.musicroom.resources.filters;

import javax.ws.rs.ext.Provider;

import org.json.JSONObject;

@Provider
@RequireLogin
public class IsLoggedFilter extends BaseAuthorizationFilter {
	private static final String NOT_LOGGED = "{\"error\":\"Access denied! please login first.\"}";
	
	@Override
	public boolean isAuthorized(JSONObject loggedUser) {
		return loggedUser != null;
	}

	@Override
	public String getErrorObject() {
		return NOT_LOGGED; 
	}

}
