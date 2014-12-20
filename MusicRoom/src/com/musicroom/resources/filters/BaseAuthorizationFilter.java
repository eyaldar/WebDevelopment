package com.musicroom.resources.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.musicroom.session.SessionManager;

public abstract class BaseAuthorizationFilter implements ContainerRequestFilter {

	@Context 
	private HttpServletRequest webRequest;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		JSONObject loggedUser = SessionManager.getLoggedInUser(webRequest);

		// Check if there is no logged user
		if (!isAuthorized(loggedUser)) {
			String errorObject = getErrorObject();
			throw new WebApplicationException(Response.status(HttpServletResponse.SC_UNAUTHORIZED)
					.entity(errorObject).build());
		}
	}
	
	public abstract boolean isAuthorized(JSONObject loggedUser);
	public abstract String getErrorObject();
}
