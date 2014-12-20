package com.musicroom.resources.filters;

import org.json.JSONObject;

import com.musicroom.utils.UserType;

@RequireLoggedBand
public class IsLoggedBandFilter extends IsLoggedFilter {
	@Override
	public boolean isAuthorized(JSONObject loggedUser) {
		
		return super.isAuthorized(loggedUser) && 
			   loggedUser.getInt("user_type_id") == UserType.BAND.toInt();
	}
}
