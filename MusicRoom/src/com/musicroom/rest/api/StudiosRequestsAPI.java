package com.musicroom.rest.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.database.MainDBHandler;
import com.musicroom.session.SessionManager;
import com.musicroom.utils.JSONUtils;

@Path("/studios")
public class StudiosRequestsAPI {

	private static final String STUDIO_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Studio with id '%d' was not found\"}";
	private static final String STUDIO_ID_ROOM_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Room with id %d in studio with id '%d' was not found\"}";
	private static final String USER_ALREADY_EXISTS_ERROR_JSON = "{\"error\":\"User name '%s' already exists\"}";

	@GET
	@Produces("application/json")
	public Response getStudios() {
		JSONArray selectResult = MainDBHandler
				.select("select r.STUDIO_ID, s.STUDIO_NAME, s.ADDRESS, s.CITY_ID, s.USER_ID, s.CONTACT_NAME, s.EMAIL, s.PHONE, "
						+ "r.ID as ROOM_ID, r.RATE, r.ROOM_NAME, s.VOTES_SUM / s.VOTES_COUNT as AVG_REVIEW "
						+ "from STUDIOS as s left join ROOMS as r on r.STUDIO_ID = s.ID");

		JSONArray result = new JSONArray();
		List<Integer> studioIDs = new ArrayList<Integer>();

		for (int i = 0; i < selectResult.length(); i++) {
			JSONObject currentRow = selectResult.getJSONObject(i);
			JSONObject currentStudio;

			int studioID = currentRow.getInt("STUDIO_ID");

			int studioIndex = studioIDs.indexOf(studioID);

			// If the studio is new in the result
			if (studioIndex == -1) {
				// Add the studio
				studioIDs.add(studioID);
				currentStudio = new JSONObject();
				currentStudio.put("ID", studioID);
				currentStudio.put("STUDIO_NAME",
						currentRow.getString("STUDIO_NAME"));
				currentStudio.put("ADDRESS", currentRow.getString("ADDRESS"));
				currentStudio.put("CITY_ID", currentRow.getInt("CITY_ID"));
				currentStudio.put("USER_ID", currentRow.getInt("USER_ID"));
				currentStudio.put("CONTACT_NAME",
						currentRow.getString("CONTACT_NAME"));
				currentStudio.put("EMAIL", currentRow.getString("EMAIL"));
				currentStudio.put("PHONE", currentRow.getString("PHONE"));

				if (currentRow.has("AVG_REVIEW"))
					currentStudio.put("AVG_REVIEW",
							currentRow.getDouble("AVG_REVIEW"));
				else
					currentStudio.put("AVG_REVIEW", "No Reviews");

				result.put(currentStudio);
			}
			// the studio exists in the result
			else {
				currentStudio = result.getJSONObject(studioIndex);
			}

			// Add the room
			JSONObject room = new JSONObject();
			room.put("ID", currentRow.getInt("ROOM_ID"));
			room.put("RATE", currentRow.getInt("RATE"));
			room.put("ROOM_NAME", currentRow.getString("ROOM_NAME"));
			currentStudio.append("ROOMS", room);
		}

		return Response.ok(result.toString()).build();
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response getStudioByID(@PathParam("id") int id) {
		JSONArray results = MainDBHandler
				.selectWithParameters(
						"select *, r.ID as ROOM_ID, s.VOTES_SUM / s.VOTES_COUNT as AVG_REVIEW "
								+ "from STUDIOS as s left join ROOMS as r on r.STUDIO_ID = s.ID "
								+ "where s.ID = ?", id);

		// Check if any result
		if (results.length() > 0) {
			// Get the first row for studio details
			JSONObject firstRow = JSONUtils.extractJSONObject(results);

			JSONObject studio = new JSONObject();
			studio.put("ID", firstRow.getInt("STUDIO_ID"));
			studio.put("STUDIO_NAME", firstRow.getString("STUDIO_NAME"));
			studio.put("ADDRESS", firstRow.getString("ADDRESS"));
			studio.put("CITY_ID", firstRow.getInt("CITY_ID"));
			studio.put("USER_ID", firstRow.getInt("USER_ID"));
			studio.put("CONTACT_NAME", firstRow.getString("CONTACT_NAME"));
			studio.put("EMAIL", firstRow.getString("EMAIL"));
			studio.put("PHONE", firstRow.getString("PHONE"));
			studio.put("SITE_URL", firstRow.getString("SITE_URL"));
			studio.put("FACEBOOK_PAGE", firstRow.getString("FACEBOOK_PAGE"));
			studio.put("LOGO_URL", firstRow.getString("LOGO_URL"));
			studio.put("EXTRA_DETAILS", firstRow.getString("EXTRA_DETAILS"));

			if (firstRow.has("AVG_REVIEW"))
				studio.put("AVG_REVIEW", firstRow.getDouble("AVG_REVIEW"));
			else
				studio.put("AVG_REVIEW", "No Reviews");

			int[] roomIDs = new int[results.length()];

			// Add rooms
			for (int i = 0; i < results.length(); i++) {
				JSONObject currentRow = results.getJSONObject(i);
				JSONObject room = new JSONObject();

				roomIDs[i] = currentRow.getInt("ROOM_ID");
				room.put("ID", roomIDs[i]);
				room.put("RATE", currentRow.getInt("RATE"));
				room.put("ROOM_NAME", currentRow.getString("ROOM_NAME"));

				studio.append("ROOMS", room);
			}

			// Add bands
			if (roomIDs.length > 0) {
				String roomIDsStr = Arrays.toString(roomIDs).replace("[", "")
						.replace("]", "");

				JSONArray bands = MainDBHandler
						.select("select distinct b.BAND_NAME "
								+ "from BANDS as b join ROOM_SCHEDULE as rs on b.ID = rs.BAND_ID "
								+ "where rs.ROOM_ID in (" + roomIDsStr + ")");

				studio.put("PLAYING_BANDS", bands);
			}

			return Response.ok(studio.toString()).build();
		} else {
			String errorJson = String.format(
					STUDIO_ID_WAS_NOT_FOUND_ERROR_JSON, id);
			return Response.status(HttpServletResponse.SC_NOT_FOUND)
					.entity(errorJson).build();
		}
	}

	@POST
	@Produces("application/json")
	@Consumes
	public Response AddStudio(String dataStr,
			@Context HttpServletRequest Request) {
		JSONObject data = new JSONObject("dataStr");
		JSONObject userObj = data.getJSONObject("user");

		JSONArray existingUser = MainDBHandler.selectWithParameters(
				"select * from USERS where USER_NAME = ?",
				userObj.getString("name"));

		// if there are results from the user check
		if (existingUser.length() > 0) {
			String errorJson = String.format(USER_ALREADY_EXISTS_ERROR_JSON,
					userObj.getString("name"));
			return Response.status(HttpServletResponse.SC_CONFLICT)
					.entity(errorJson).build();
		} else {
			try {
				Connection conn = MainDBHandler.getConnection();
				conn.setAutoCommit(false);

				// Add the user
				ResultSet rs = MainDBHandler
						.insertWithAutoKey(
								"INSERT INTO USERS (USER_NAME, PASSWORD, USER_TYPE_ID) VALUES(?, ?, 1)",
								PreparedStatement.RETURN_GENERATED_KEYS,
								userObj.getString("USER_NAME"),
								userObj.getString("PASSWORD"));
				if (!rs.next()) {
					conn.rollback();
				}

				int userID = rs.getInt(1);

				userObj.put("USER_TYPE_ID", 1);
				userObj.put("ID", userID);

				// Add studio
				JSONObject studioObj = data.getJSONObject("studio");

				rs = MainDBHandler
						.insertWithAutoKey(
								"INSERT INTO STUDIOS (STUDIO_NAME, CITY_ID, ADDRESS, EMAIL, CONTACT_NAME, PHONE, USER_ID, EXTRA_DETAILS, "
										+ " FACEBOOK_PAGE, SITE_URL, LOGO_URL, VOTES_SUM, VOTES_COUNT) "
										+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0)",
								PreparedStatement.RETURN_GENERATED_KEYS,
								studioObj.getString("name"),
								studioObj.getInt("city_id"),
								studioObj.getString("address"),
								studioObj.getString("email"),
								studioObj.getString("contact_name"),
								studioObj.getString("phone"), userID,
								studioObj.getString("extra_details"),
								studioObj.getString("facebook_page"),
								studioObj.getString("site"),
								studioObj.getString("logo"));

				if (!rs.next()) {
					conn.rollback();
				}

				int studioID = rs.getInt(1);

				// Add rooms
				JSONArray roomsArray = studioObj.getJSONArray("rooms");

				for (int i = 0; i < roomsArray.length(); i++) {
					// Add room
					JSONObject roomObj = roomsArray.getJSONObject(i);

					rs = MainDBHandler
							.insertWithAutoKey(
									"INSERT INTO ROOMS (STUDIO_ID, RATE, ROOM_NAME, EXTRA_DETAILS) VALUES(?, ?, ?, ?)",
									PreparedStatement.RETURN_GENERATED_KEYS,
									studioID, roomObj.getInt("rate"),
									roomObj.getString("name"),
									roomObj.getString("extra_details"));

					if (!rs.next()) {
						conn.rollback();
					}

					int roomID = rs.getInt(1);

					// Add room types
					JSONArray roomsTypesArray = roomObj
							.getJSONArray("room_type");

					for (int k = 0; k < roomsTypesArray.length(); k++) {
						rs = MainDBHandler
								.insert("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) VALUES (?, ?)",
										roomID, roomsTypesArray.getInt(k));
					}

					// Add room equipments
					JSONArray roomsEquipmentArray = roomObj
							.getJSONArray("equipment");

					for (int k = 0; k < roomsEquipmentArray.length(); k++) {
						JSONObject equipObj = roomsEquipmentArray
								.getJSONObject(k);

						rs = MainDBHandler
								.insert("INSERT INTO ROOM_EQUIPMENT (ROOM_ID, EQUIPMENT_TYPE_ID, MODEL, MANUFACTURER, QUANTITY) VALUES(?,?,?,?,?)",
										roomID, equipObj.getInt("type"),
										equipObj.getString("model"),
										equipObj.getString("manufacturer"),
										equipObj.getInt("quantity"));
					}
				}

				MainDBHandler.getConnection().commit();
				MainDBHandler.getConnection().setAutoCommit(true);

				// Set user as logged in session
				SessionManager.setLoggedInUser(Request, userObj);

				return Response.ok("{message: \"success\"}").build();
			} catch (SQLException e) {
				return Response.status(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@GET
	@Path("/{studioID}/{roomID}")
	@Produces("application/json")
	public Response getStudioRoomByID(@PathParam("studioID") int studioID,
			@PathParam("roomID") int roomID) {
		JSONArray results = MainDBHandler
				.selectWithParameters(
						"select * "
								+ "from 	ROOMS as r  left join ROOM_ROOM_TYPES as rrt on r.ID = rrt.ROOM_ID "
								+ "where r.ID = ?", roomID);

		// Check if any result
		if (results.length() > 0) {
			// Get the first row for studio details
			JSONObject firstRow = JSONUtils.extractJSONObject(results);

			JSONObject room = new JSONObject();
			room.put("STUDIO_ID", firstRow.getInt("STUDIO_ID"));
			room.put("ID", firstRow.getInt("ROOM_ID"));
			room.put("ROOM_NAME", firstRow.getString("ROOM_NAME"));
			room.put("RATE", firstRow.getInt("RATE"));
			room.put("EXTRA_DETAILS", firstRow.getString("EXTRA_DETAILS"));

			// Add room types
			for (int i = 0; i < results.length(); i++) {
				JSONObject currentRow = results.getJSONObject(i);
				room.append("ROOM_TYPE", currentRow.getInt("TYPE_ID"));
			}

			// Add equipment
			JSONArray equipment = MainDBHandler.selectWithParameters(
					"select * " + "from ROOM_EQUIPMENT as re "
							+ "where re.ROOM_ID = ?", roomID);

			for (int i = 0; i < equipment.length(); i++) {
				JSONObject currentRow = equipment.getJSONObject(i);
				room.append("EQUIPMENT", currentRow);
			}

			return Response.ok(room.toString()).build();
		} else {
			String errorJson = String.format(
					STUDIO_ID_ROOM_ID_WAS_NOT_FOUND_ERROR_JSON, roomID,
					studioID);
			return Response.status(HttpServletResponse.SC_NOT_FOUND)
					.entity(errorJson).build();
		}
	}
}
