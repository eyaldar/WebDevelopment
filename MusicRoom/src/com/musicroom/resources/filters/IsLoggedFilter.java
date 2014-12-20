package com.musicroom.resources.filters;

import org.json.JSONObject;

@RequireLogin
public class IsLoggedFilter extends BaseAuthorizationFilter {

	@Override
	public boolean isAuthorized(JSONObject loggedUser) {
		return loggedUser != null;
	}

}
