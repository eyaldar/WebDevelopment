package com.musicroom.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.musicroom.utils.BandJSONArrayGenerator;
import com.musicroom.utils.JSONUtils;
import com.musicroom.utils.UserType;
import com.musicroom.utils.UsersTableUtils;

@Path("/bands")
public class BandsResource {

	private static final int SPACE_TO_INDENTS_EACH_LEVEL = 2;
	private static final String BAND_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Band with id '%d' was not found\"}";
	private static final String GET_BANDS_SELECT_QUERY = "select * from BANDS as b "
			+ "left join BAND_MEMBERS as bm "
			+ "on b.ID = bm.BAND_ID "
			+ "left join MEMBER_INSTRUMENT as bi "
			+ "on bm.ID = bi.MEMBER_ID "
			+ "left join EQUIPMENT_TYPES as et "
			+ "on et.ID = bi.EQUIPMENT_TYPE_ID";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBands() throws Exception {
		JSONArray results = MainDBHandler.select(GET_BANDS_SELECT_QUERY);

		BandJSONArrayGenerator bandsArrayGen = new BandJSONArrayGenerator();
		JSONArray bandsArray = bandsArrayGen.createBandsArray(results);

		return bandsArray.toString(SPACE_TO_INDENTS_EACH_LEVEL);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBandByID(@PathParam("id") int id) throws Exception {
		JSONArray results = MainDBHandler.selectWithParameters(
				GET_BANDS_SELECT_QUERY + " where b.id = ?", id);

		BandJSONArrayGenerator bandsArrayGen = new BandJSONArrayGenerator();
		JSONArray bandsArray = bandsArrayGen.createBandsArray(results);
		JSONObject result = JSONUtils.extractJSONObject(bandsArray);

		if (result.length() > 0) {
			return Response.ok(result.toString(SPACE_TO_INDENTS_EACH_LEVEL))
					.build();
		} else {
			String errorJson = String.format(BAND_ID_WAS_NOT_FOUND_ERROR_JSON,
					id);
			return Response.status(HttpServletResponse.SC_NOT_FOUND)
					.entity(errorJson).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addBand(String dataStr, @Context HttpServletRequest Request)
			throws Exception {
		try {
			JSONObject data = new JSONObject(dataStr);
			JSONObject userObj = data.getJSONObject("user");

			// if there are results from the user check
			if (UsersTableUtils.isExistingUser(userObj.getString("name"))) {
				String errorJson = String.format(
						UsersTableUtils.USER_ALREADY_EXISTS_ERROR_JSON,
						userObj.getString("name"));
				return Response.status(HttpServletResponse.SC_CONFLICT)
						.entity(errorJson).build();
			} else {
				Connection conn = MainDBHandler.getConnection();
				conn.setAutoCommit(false);

				// Add the user
				ResultSet rs;
				int userID = UsersTableUtils.addUser(userObj.getString("name"),
						userObj.getString("password"), UserType.BAND);

				if (userID == -1) {
					conn.rollback();
					return Response.serverError().build();
				}

				userObj.put("USER_TYPE_ID", UserType.BAND.toInt());
				userObj.put("ID", userID);

				// Add band
				JSONObject bandObj = data.getJSONObject("band");

				rs = MainDBHandler
						.insertWithAutoKey(
								"INSERT INTO BANDS (BAND_NAME, GENRE, USER_ID, LOGO_URL) VALUES(?, ?, ?, ?)",
								PreparedStatement.RETURN_GENERATED_KEYS,
								bandObj.getString("name"),
								bandObj.getString("genre"), userID,
								bandObj.getString("logo"));

				if (!rs.next()) {
					conn.rollback();
					return Response.serverError().build();
				}

				int bandID = rs.getInt(1);

				// Add band members
				// Add room types
				JSONArray bandMembersArray = bandObj
						.getJSONArray("band_members");
				for (int i = 0; i < bandMembersArray.length(); i++) {
					JSONObject bandMember = bandMembersArray.getJSONObject(i);

					rs = MainDBHandler
							.insertWithAutoKey(
									"INSERT INTO BAND_MEMBERS (MEMBER_NAME, ROLE, BAND_ID, PICTURE_URL) VALUES(?, ?, ?, ?)",
									PreparedStatement.RETURN_GENERATED_KEYS,
									bandMember.getString("name"),
									bandMember.getString("role"), bandID,
									bandMember.getString("picture"));

					if (!rs.next()) {
						conn.rollback();
						return Response.serverError().build();
					}

					int bandMemberID = rs.getInt(1);

					JSONArray instrumentsArray = bandMember
							.getJSONArray("instruments");

					for (int k = 0; k < instrumentsArray.length(); k++) {
						MainDBHandler
								.executeUpdateWithParameters(
										"INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) VALUES(?, ?)",
										bandMemberID,
										instrumentsArray.getInt(k));
					}
				}

				conn.commit();
				conn.setAutoCommit(true);

				// Set user as logged in session
				SessionManager.setLoggedInUser(Request, userObj);

				return Response.ok("{message: \"success\"}").build();
			}
		} catch (Exception e) {
			e.printStackTrace();

			MainDBHandler.getConnection().rollback();
			throw e;
		}
	}
}
