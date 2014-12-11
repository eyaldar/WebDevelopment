package com.musicroom.rest.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.database.MainDBHandler;
import com.musicroom.session.SessionManager;
import com.musicroom.utils.JSONUtils;

@Path("/login")
public class LoginRequestsAPI {
	private static final String BAD_LOGIN = "{\"error\":\"Invalid user name or password.\"}";

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Login(String dataStr, @Context HttpServletRequest Request) {
		JSONObject loginData = new JSONObject(dataStr);

		// Get user by name and password
		JSONArray loggedRes;
		try {
			loggedRes = MainDBHandler.selectWithParameters(
					"select * from USERS where USER_NAME = ? and PASSWORD = ?",
					loginData.getString("USER_NAME"),
					loginData.getString("PASSWORD"));

			// Check if there is a result
			if (loggedRes.length() == 0) {
				return Response.status(HttpServletResponse.SC_NOT_FOUND)
						.entity(BAD_LOGIN).build();
			} else {
				// Get the user
				JSONObject loggedUser = JSONUtils.extractJSONObject(loggedRes);

				// Set user as logged in session
				SessionManager.setLoggedInUser(Request, loggedUser);

				return Response.ok(loggedUser.toString()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
}
