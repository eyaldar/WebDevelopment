package com.musicroom.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.musicroom.database.MainDBHandler;
import com.musicroom.database.RedisManager;
import com.musicroom.session.SessionManager;
import com.musicroom.utils.JSONUtils;
import com.musicroom.utils.UserType;
import com.musicroom.utils.UsersTableUtils;

@Path("/studios")
public class StudiosResource {

	private static final int SPACE_TO_INDENTS_EACH_LEVEL = 2;
	private static final String STUDIO_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Studio with id '%d' was not found\"}";
	private static final String STUDIO_ID_ROOM_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Room with id %d in studio with id '%d' was not found\"}";

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudioByID(@PathParam("id") int id) {
		try {
			JSONArray results = MainDBHandler
					.selectWithParameters(
							"select *, r.ID as ROOM_ID "
									+ "from STUDIOS as s left join ROOMS as r on r.STUDIO_ID = s.ID "
									+ "where s.ID = ?", id);

			// Check if any result
			if (results.length() > 0) {
				// Get the first row for studio details
				JSONObject firstRow = JSONUtils.extractJSONObject(results);

				JSONObject studio = new JSONObject();
				studio.put("id", firstRow.getInt("STUDIO_ID"));
				studio.put("studio_name", firstRow.getString("STUDIO_NAME"));
				studio.put("address", firstRow.getString("ADDRESS"));
				studio.put("city_id", firstRow.getInt("CITY_ID"));
				studio.put("user_id", firstRow.getInt("USER_ID"));
				studio.put("contact_name", firstRow.getString("CONTACT_NAME"));
				studio.put("email", firstRow.getString("EMAIL"));
				studio.put("phone", firstRow.getString("PHONE"));
				studio.put("site_url", firstRow.getString("SITE_URL"));
				studio.put("facebook_page", firstRow.getString("FACEBOOK_PAGE"));
				studio.put("logo_url", firstRow.getString("LOGO_URL"));
				studio.put("extra_details", firstRow.getString("EXTRA_DETAILS"));

				// Add avg rating
				JSONArray avg_rating = MainDBHandler.selectWithParameters(
						"select avg(RATING) as AVG_RATING " + "from REVIEWS "
								+ "where STUDIO_ID = ?", id);

				studio.put("avg_rating",
						avg_rating.getJSONObject(0).getDouble("AVG_RATING"));

				int[] roomIDs = new int[results.length()];

				// Add rooms
				for (int i = 0; i < results.length(); i++) {
					JSONObject currentRow = results.getJSONObject(i);
					JSONObject room = new JSONObject();

					roomIDs[i] = currentRow.getInt("ROOM_ID");
					room.put("id", roomIDs[i]);
					room.put("rate", currentRow.getInt("RATE"));
					room.put("name", currentRow.getString("ROOM_NAME"));

					studio.append("rooms", room);
				}

				// Add bands
				if (roomIDs.length > 0) {
					String roomIDsStr = Arrays.toString(roomIDs)
							.replace("[", "").replace("]", "");

					JSONArray bands = MainDBHandler
							.select("select distinct b.BAND_NAME as name "
									+ "from BANDS as b join ROOM_SCHEDULE as rs on b.ID = rs.BAND_ID "
									+ "where rs.ROOM_ID in (" + roomIDsStr
									+ ")");
					
					studio.put("playing_bands", bands);
				}

				return Response.ok(studio.toString(SPACE_TO_INDENTS_EACH_LEVEL)).build();
			} else {
				String errorJson = String.format(
						STUDIO_ID_WAS_NOT_FOUND_ERROR_JSON, id);
				return Response.status(HttpServletResponse.SC_NOT_FOUND)
						.entity(errorJson).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addStudio(String dataStr,
			@Context HttpServletRequest Request) {
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
						userObj.getString("password"), UserType.STUDIO);

				if (userID == -1) {
					conn.rollback();
					return Response.serverError().build();
				}
				;

				userObj.put("USER_TYPE_ID", UserType.STUDIO.toInt());
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
					return Response.serverError().build();
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
						return Response.serverError().build();
					}

					int roomID = rs.getInt(1);

					// Add room types
					JSONArray roomsTypesArray = roomObj
							.getJSONArray("room_type");

					for (int k = 0; k < roomsTypesArray.length(); k++) {
						MainDBHandler
								.executeUpdateWithParameters(
										"INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) VALUES (?, ?)",
										roomID, roomsTypesArray.getInt(k));
					}

					// Add room equipments
					JSONArray roomsEquipmentArray = roomObj
							.getJSONArray("equipment");

					for (int k = 0; k < roomsEquipmentArray.length(); k++) {
						JSONObject equipObj = roomsEquipmentArray
								.getJSONObject(k);

						MainDBHandler
								.executeUpdateWithParameters(
										"INSERT INTO ROOM_EQUIPMENT (ROOM_ID, EQUIPMENT_TYPE_ID, MODEL, MANUFACTURER, QUANTITY) VALUES(?,?,?,?,?)",
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

				// Add new studio's ID to the NEW_STUDIOS list.
				RedisManager.getConnection().lpush("NEW_STUDIOS",
						String.valueOf(studioID));
				RedisManager.getConnection().ltrim("NEW_STUDIOS", 0, 9);

				return Response.ok("{message: \"success\"}").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	@GET
	@Path("/{studioID}/{roomID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudioRoomByID(@PathParam("studioID") int studioID,
			@PathParam("roomID") int roomID) {
		try {
			JSONArray results = MainDBHandler
					.selectWithParameters(
							"select * "
									+ "from ROOMS as r left join ROOM_ROOM_TYPES as rrt on r.ID = rrt.ROOM_ID "
									+ "where r.ID = ?", roomID);

			// Check if any result
			if (results.length() > 0) {
				// Get the first row for studio details
				JSONObject firstRow = JSONUtils.extractJSONObject(results);

				JSONObject room = new JSONObject();
				room.put("studio_id", firstRow.getInt("STUDIO_ID"));
				room.put("id", firstRow.getInt("ROOM_ID"));
				room.put("name", firstRow.getString("ROOM_NAME"));
				room.put("rate", firstRow.getInt("RATE"));
				room.put("extra_details", firstRow.getString("EXTRA_DETAILS"));

				// Add room types
				for (int i = 0; i < results.length(); i++) {
					JSONObject currentRow = results.getJSONObject(i);
					room.append("room_type", currentRow.getInt("TYPE_ID"));
				}

				// Add equipment
				JSONArray equipment = MainDBHandler.selectWithParameters(
						"select * " + "from ROOM_EQUIPMENT as re "
								+ "where re.ROOM_ID = ?", roomID);

				for (int i = 0; i < equipment.length(); i++) {
					JSONObject currentRow = equipment.getJSONObject(i);
					room.append("equipment", currentRow);
				}

				return Response.ok(room.toString(SPACE_TO_INDENTS_EACH_LEVEL)).build();
			} else {
				String errorJson = String.format(
						STUDIO_ID_ROOM_ID_WAS_NOT_FOUND_ERROR_JSON, roomID,
						studioID);
				return Response.status(HttpServletResponse.SC_NOT_FOUND)
						.entity(errorJson).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchStuidos(@Context UriInfo info) {
		try {
			String result;
			String queryKey = getRedisQueryKey(info.getQueryParameters());
			Jedis redisConn = RedisManager.getConnection();

			if (!redisConn.exists(queryKey)) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				// Get the params
				Integer areaId = null;
				Integer cityId = null;
				Integer roomTypeId = null;
				Integer equipTypeId = null;
				Integer equipCatId = null;
				Integer maxRoomRate = null;
				Date startTime = null;
				Date endTime = null;

				String name = info.getQueryParameters().getFirst("name");
				String areaIdStr = info.getQueryParameters()
						.getFirst("area_id");
				String cityIdStr = info.getQueryParameters()
						.getFirst("city_id");
				String roomTypeIdStr = info.getQueryParameters().getFirst(
						"room_type_id");
				String equipTypeIdStr = info.getQueryParameters().getFirst(
						"equipment_type_id");
				String equipCatIdStr = info.getQueryParameters().getFirst(
						"equipment_category_id");
				String maxRoomRateStr = info.getQueryParameters().getFirst(
						"max_room_rate");
				String startTimeStr = info.getQueryParameters().getFirst(
						"start_time");
				String endTimeStr = info.getQueryParameters().getFirst(
						"end_time");

				if (areaIdStr != null)
					areaId = Integer.parseInt(areaIdStr);
				if (cityIdStr != null)
					cityId = Integer.parseInt(cityIdStr);
				if (roomTypeIdStr != null)
					roomTypeId = Integer.parseInt(roomTypeIdStr);
				if (equipTypeIdStr != null)
					equipTypeId = Integer.parseInt(equipTypeIdStr);
				if (equipCatIdStr != null)
					equipCatId = Integer.parseInt(equipCatIdStr);
				if (maxRoomRateStr != null)
					maxRoomRate = Integer.parseInt(maxRoomRateStr);
				if (startTimeStr != null)
					startTime = format.parse(startTimeStr);
				if (endTimeStr != null)
					endTime = format.parse(endTimeStr);

				// Build sql query
				String sqlQuery = "select r.STUDIO_ID, s.STUDIO_NAME, s.ADDRESS, s.CITY_ID, s.USER_ID, s.CONTACT_NAME, s.EMAIL, s.PHONE, "
						+ "r.ID as ROOM_ID, r.RATE, r.ROOM_NAME "
						+ "from STUDIOS as s left join ROOMS as r on r.STUDIO_ID = s.ID "
						+ "where 1=1 ";

				if (name != null) {
					sqlQuery += "and s.STUDIO_NAME like '%" + name + "%' ";
				}

				if (areaId != null) {
					sqlQuery += "and exists (select 1 " + "from CITIES as c "
							+ "where c.ID = s.CITY_ID and c.AREA_ID = "
							+ areaId + ") ";
				}

				if (cityId != null) {
					sqlQuery += "and s.CITY_ID = " + cityId + " ";
				}

				if (maxRoomRate != null) {
					sqlQuery += "and r.RATE <= " + maxRoomRate + " ";
				}

				if (roomTypeId != null) {
					sqlQuery += "and exists (select 1 "
							+ "from ROOM_ROOM_TYPES as rrt "
							+ "where rrt.ROOM_ID = r.ID and rrt.TYPE_ID = "
							+ roomTypeId + ") ";
				}

				if (equipTypeId != null) {
					sqlQuery += "and exists (select 1 "
							+ "from ROOM_EQUIPMENT as re "
							+ "where re.ROOM_ID = r.ID and re.EQUIPMENT_TYPE_ID = "
							+ equipTypeId + ") ";
				}

				if (equipCatId != null) {
					sqlQuery += "and exists (select 1 "
							+ "from ROOM_EQUIPMENT as re, EQUIPMENT_TYPES as et "
							+ "where re.ROOM_ID = r.ID and re.EQUIPMENT_TYPE_ID =  et.ID and et.CATEGORY_ID = "
							+ equipCatId + ") ";
				}

				if (startTime != null && endTime != null) {
					sqlQuery += "and not exists (select 1 "
							+ "from ROOM_SCHEDULE as rs "
							+ "where rs.ROOM_ID = r.ID and rs.START_TIME < "
							+ format.format(endTime) + " and rs.END_TIME > "
							+ format.format(startTime) + ") ";
				}

				JSONArray selectResult = MainDBHandler.select(sqlQuery);

				JSONArray arrayResult = new JSONArray();
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
						currentStudio.put("id", studioID);
						currentStudio.put("studio_name",
								currentRow.getString("STUDIO_NAME"));
						currentStudio.put("address",
								currentRow.getString("ADDRESS"));
						currentStudio.put("city_id",
								currentRow.getInt("CITY_ID"));
						currentStudio.put("user_id",
								currentRow.getInt("USER_ID"));
						currentStudio.put("contact_name",
								currentRow.getString("CONTACT_NAME"));
						currentStudio.put("email",
								currentRow.getString("EMAIL"));
						currentStudio.put("phone",
								currentRow.getString("PHONE"));

						// Add avg rating
						JSONArray avg_rating = MainDBHandler
								.selectWithParameters(
										"select avg(RATING) as AVG_RATING "
												+ "from REVIEWS "
												+ "where STUDIO_ID = ?",
										studioID);

						currentStudio.put("avg_rating", avg_rating
								.getJSONObject(0).getDouble("AVG_RATING"));

						arrayResult.put(currentStudio);
					}
					// the studio exists in the result
					else {
						currentStudio = arrayResult.getJSONObject(studioIndex);
					}

					// Add the room
					JSONObject room = new JSONObject();
					room.put("id", currentRow.getInt("ROOM_ID"));
					room.put("rate", currentRow.getInt("RATE"));
					room.put("name", currentRow.getString("ROOM_NAME"));
					currentStudio.append("rooms", room);

				} 
				
				result = arrayResult.toString(SPACE_TO_INDENTS_EACH_LEVEL);
				
				redisConn.set(queryKey, result);
				redisConn.expire(queryKey, 300);
			} else {
				result = redisConn.get(queryKey);
			}
			
			return Response.ok(result).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	private String getRedisQueryKey(MultivaluedMap<String, String> queryMap) {
		StringBuilder stringBuilder = new StringBuilder("QUERY");
		Iterator<Entry<String, List<String>>> it = queryMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, List<String>> pairs = it.next();
			stringBuilder.append(":");
			stringBuilder
					.append(pairs.getKey() + "=" + pairs.getValue().get(0));
		}

		return stringBuilder.toString();
	}
}
