package com.musicroom.resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.database.MainDBHandler;
import com.musicroom.session.SessionManager;
import com.musicroom.utils.JSONUtils;

@Path("/users")
public class UsersResource {
	private static final String SUCCESS = "{ \"message\": \"success\"}";
	private static final String BAD_LOGIN = "{\"error\":\"Invalid user name or password.\"}";
	private static final String UNAUTHORIZED_DELETE = "{\"error\":\"Attempt to delete user without being logged-in.\"}";
	private static final String UNAUTHORIZED_UPDATE = "{\"error\":\"Attempt to update user without being logged-in.\"}";
	private static final int SPACE_TO_INDENTS_EACH_LEVEL = 2;

	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	public static Response getUserTypes() throws Exception {
		JSONArray result = MainDBHandler
				.select("select DESCRIPTION as name, ID as id "
						+ "from USER_TYPES");

		return Response.ok(result.toString(SPACE_TO_INDENTS_EACH_LEVEL))
				.build();
	}
	
	@GET
	@Path("/state")
	@Produces(MediaType.APPLICATION_JSON)
	public static Response getLoggedInUser(@Context HttpServletRequest request) throws Exception {
		JSONObject loggedUser = SessionManager.getLoggedInUser(request);
		JSONObject result = new JSONObject();
		
		if (loggedUser == null) {
			result.put("name", "Anonymous");
			result.put("logged", false);
		} else {
			result.put("name", loggedUser.getString("USER_NAME"));
			result.put("user", loggedUser);
			result.put("logged", true);
		}
		
		return Response.ok(result.toString(SPACE_TO_INDENTS_EACH_LEVEL))
				.build();
	}
	
	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public static Response logoutUser(@Context HttpServletRequest request) throws Exception {
		// log out
		SessionManager.logoutUser(request);

		return Response.ok(SUCCESS).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(String dataStr, @Context HttpServletRequest request) {
		JSONObject loginData = new JSONObject(dataStr);

		// Get user by name and password
		JSONArray loggedRes;
		try {
			loggedRes = MainDBHandler.selectWithParameters(
					"select * from USERS where USER_NAME = ? and PASSWORD = ?",
					loginData.getString("name"),
					loginData.getString("password"));

			// Check if there is a result
			if (loggedRes.length() == 0) {
				return Response.status(HttpServletResponse.SC_NOT_FOUND)
						.entity(BAD_LOGIN).build();
			} else {
				// Get the user
				JSONObject loggedUser = JSONUtils.extractJSONObject(loggedRes);

				// Set user as logged in session
				SessionManager.setLoggedInUser(request, loggedUser);

				return Response.ok(loggedUser.toString()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response deleteUser(@PathParam("id") int id,
			@Context HttpServletRequest Request) throws Exception {
		// Validate user
		if (!SessionManager.validateUser(Request, id)) {
			// Unauthorized
			return Response.notModified(UNAUTHORIZED_DELETE).build();
		} else {
			// delete
			MainDBHandler.executeUpdateWithParameters(
					"delete from USERS where ID = ?", id);

			// log out
			SessionManager.logoutUser(Request);

			return Response.ok(SUCCESS).build();
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePassword(String strData,
			@Context HttpServletRequest Request) throws Exception {
		JSONObject data = new JSONObject(strData);

		int userID = data.getInt("ID");

		// Validate user
		if (!SessionManager.validateUser(Request, userID)) {
			// Unauthorized
			return Response.notModified(UNAUTHORIZED_UPDATE).build();
		} else {
			String password = data.getString("password");

			// update
			MainDBHandler.executeUpdateWithParameters(
					"update USERS set PASSWORD = ? where ID = ?", password,
					userID);

			// update session
			SessionManager.updateLoggedUserPassword(Request, password);

			return Response.ok(SUCCESS).build();
		}
	}
}
