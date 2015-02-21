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
import java.util.Set;

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
import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import com.musicroom.database.MainDBHandler;
import com.musicroom.database.RedisManager;
import com.musicroom.resources.filters.RequireLoggedBand;
import com.musicroom.resources.filters.RequireLoggedStudio;
import com.musicroom.session.SessionManager;
import com.musicroom.utils.JSONUtils;
import com.musicroom.utils.UserType;
import com.musicroom.utils.UsersTableUtils;

@Path("/studios")
public class StudiosResource {

	private static final int SPACE_TO_INDENTS_EACH_LEVEL = 2;
	private static final String STUDIO_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Studio with id '%d' was not found\"}";
	private static final String STUDIO_ID_ROOM_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Room with id %d in studio with id '%d' was not found\"}";
	private static final String APPIONTMENT_UNAVAILABLE_ERROR_JSON = "{\"error\":\"Room with id %d is not available in the wanted time\"}";

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
				studio.put("id", firstRow.getInt("studio_id"));
				studio.put("name", firstRow.getString("studio_name"));
				studio.put("address", firstRow.getString("address"));
				studio.put("city_id", firstRow.getInt("city_id"));
				studio.put("user_id", firstRow.getInt("user_id"));
				studio.put("contact_name", firstRow.getString("contact_name"));
				studio.put("email", firstRow.getString("email"));
				studio.put("phone", firstRow.getString("phone"));
				studio.put("site_url", firstRow.getString("site_url"));
				studio.put("facebook_page", firstRow.getString("facebook_page"));
				studio.put("logo", firstRow.getString("logo_url"));
				studio.put("extra_details", firstRow.getString("extra_details"));

				// Add avg rating
				JSONArray avg_rating = MainDBHandler.selectWithParameters(
						"select avg(RATING) as avg_rating " + "from REVIEWS "
								+ "where STUDIO_ID = ?", id);

				if (avg_rating.getJSONObject(0).has("avg_rating")) {
					// Add avg
					studio.put("avg_rating", avg_rating.getJSONObject(0)
							.getDouble("avg_rating"));
				} else {
					// no avg
					studio.put("avg_rating", "no reviews");
				}

				int[] roomIDs = new int[results.length()];

				// Add rooms
				for (int i = 0; i < results.length(); i++) {
					JSONObject currentRow = results.getJSONObject(i);
					JSONObject room = new JSONObject();

					roomIDs[i] = currentRow.getInt("room_id");
					room.put("id", roomIDs[i]);
					room.put("rate", currentRow.getInt("rate"));
					room.put("name", currentRow.getString("room_name"));

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

				// Create JSON for cache - only name and id
				JSONObject forCache = new JSONObject();
				forCache.put("id", studio.get("id"));
				forCache.put("name", studio.get("name"));

				// increase the views of the studio
				Jedis redisConn = RedisManager.getConnection();
				int views = redisConn.zincrby("MOST_VIEWED", 1,
						forCache.toString()).intValue();
				RedisManager.returnResource(redisConn);

				studio.put("views", views);

				return Response
						.ok(studio.toString(SPACE_TO_INDENTS_EACH_LEVEL))
						.build();
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

	@GET
	@RequireLoggedStudio
	@Path("/schedule/{roomId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppointment(@PathParam("roomId") int roomId,
			@Context HttpServletRequest request) throws JSONException,
			Exception {
		JSONObject user = SessionManager.getLoggedInUser(request);

		// Check that room is clear
		JSONArray queryRes = MainDBHandler.selectWithParameters("select rs.* "
				+ " from ROOM_SCHEDULE as rs"
				+ " left join ROOMS as r on r.ID = rs.ROOM_ID"
				+ " left join STUDIOS as s on s.ID = r.STUDIO_ID"
				+ " where rs.ROOM_ID = ? and s.USER_ID = ?", roomId,
				user.getInt("id"));

		return Response.ok(queryRes.toString(SPACE_TO_INDENTS_EACH_LEVEL))
				.build();
	}

	@POST
	@Path("/schedule")
	@RequireLoggedBand
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setAppointment(String dataStr,
			@Context HttpServletRequest request) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			// Get request data
			JSONObject data = new JSONObject(dataStr);
			int roomId = data.getInt("roomId");
			Date start = format.parse(data.getString("start"));
			Date end = format.parse(data.getString("end"));

			// Check that room is clear
			JSONArray queryRes = MainDBHandler
					.select("select count(*) as count from ROOM_SCHEDULE "
							+ "where ROOM_ID = " + roomId
							+ " and START_TIME < '" + format.format(end)
							+ "' and END_TIME > '" + format.format(start) + "'");

			// if there are appointments in the wanted time
			if (queryRes.getJSONObject(0).getInt("count") > 0) {
				String errorJson = String.format(
						APPIONTMENT_UNAVAILABLE_ERROR_JSON, roomId);
				return Response.status(HttpServletResponse.SC_CONFLICT)
						.entity(errorJson).build();
			} else {

				// Get user data
				JSONObject user = SessionManager.getLoggedInUser(request);
				queryRes = MainDBHandler.selectWithParameters(
						"select ID from BANDS where USER_ID = ?",
						user.getInt("id"));
				int bandId = queryRes.getJSONObject(0).getInt("id");

				// insert the appointment
				MainDBHandler.insertWithAutoKey(
						"insert into ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) "
								+ "values(" + roomId + "," + bandId + ",'"
								+ format.format(start) + "','"
								+ format.format(end) + "')",
						PreparedStatement.NO_GENERATED_KEYS);

				return Response.ok("{\"message\": \"success\"}").build();
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

				userObj.put("user_type_id", UserType.STUDIO.toInt());
				userObj.put("id", userID);

				// Add studio
				JSONObject studioObj = data.getJSONObject("studio");

				rs = MainDBHandler
						.insertWithAutoKey(
								"INSERT INTO STUDIOS (STUDIO_NAME, CITY_ID, ADDRESS, EMAIL, CONTACT_NAME, PHONE, USER_ID, EXTRA_DETAILS, "
										+ " FACEBOOK_PAGE, SITE_URL, LOGO_URL) "
										+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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

				// Set user as logged in session
				SessionManager.setLoggedInUser(Request, userObj);

				// Create JSON for cache - only name and id
				JSONObject forCache = new JSONObject();
				forCache.put("id", studioID);
				forCache.put("name", studioObj.get("name"));

				// Add new studio to the NEW_STUDIOS list.
				Jedis redisConn = RedisManager.getConnection();

				redisConn.lpush("NEW_STUDIOS", forCache.toString());
				redisConn.ltrim("NEW_STUDIOS", 0, 9);

				RedisManager.returnResource(redisConn);

				MainDBHandler.getConnection().commit();
				MainDBHandler.getConnection().setAutoCommit(true);

				JSONObject resultResponse = new JSONObject();
				resultResponse.put("message", "succees");
				resultResponse.put("studio_id", studioID);
				resultResponse.put("user_id", userID);

				return Response.ok(resultResponse.toString()).build();
			}
		} catch (Exception e) {

			// rollback
			try {
				MainDBHandler.getConnection().rollback();
			} catch (Exception e1) {
			}

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
				room.put("studio_id", firstRow.getInt("studio_id"));
				room.put("id", firstRow.getInt("room_id"));
				room.put("name", firstRow.getString("room_name"));
				room.put("rate", firstRow.getInt("rate"));
				room.put("extra_details", firstRow.getString("extra_details"));

				// Add room types
				for (int i = 0; i < results.length(); i++) {
					JSONObject currentRow = results.getJSONObject(i);
					room.append("room_type", currentRow.getInt("type_id"));
				}

				// Add equipment
				JSONArray equipment = MainDBHandler.selectWithParameters(
						"select * " + "from ROOM_EQUIPMENT as re "
								+ "where re.ROOM_ID = ?", roomID);

				for (int i = 0; i < equipment.length(); i++) {
					JSONObject currentRow = equipment.getJSONObject(i);
					room.append("equipment", currentRow);
				}

				return Response.ok(room.toString(SPACE_TO_INDENTS_EACH_LEVEL))
						.build();
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
	public Response search(@Context UriInfo info) {
		Jedis redisConn = RedisManager.getConnection();
		
		try {
			String result;
			String queryKey = getRedisQueryKey(info.getQueryParameters());

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

				// Build SQL query
				String sqlQuery = "select r.STUDIO_ID, s.STUDIO_NAME, s.ADDRESS, s.CITY_ID, s.USER_ID, s.CONTACT_NAME, s.EMAIL, s.PHONE, s.LOGO_URL, "
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
							+ "where rs.ROOM_ID = r.ID and rs.START_TIME < '"
							+ format.format(endTime) + "' and rs.END_TIME > '"
							+ format.format(startTime) + "') ";
				}

				JSONArray selectResult = MainDBHandler.select(sqlQuery);

				JSONArray arrayResult = new JSONArray();
				List<Integer> studioIDs = new ArrayList<Integer>();

				for (int i = 0; i < selectResult.length(); i++) {
					JSONObject currentRow = selectResult.getJSONObject(i);
					JSONObject currentStudio;

					int studioID = currentRow.getInt("studio_id");

					int studioIndex = studioIDs.indexOf(studioID);

					// If the studio is new in the result
					if (studioIndex == -1) {
						// Add the studio
						studioIDs.add(studioID);
						currentStudio = new JSONObject();
						currentStudio.put("id", studioID);
						currentStudio.put("name",
								currentRow.getString("studio_name"));
						currentStudio.put("address",
								currentRow.getString("address"));
						currentStudio.put("city_id",
								currentRow.getInt("city_id"));
						currentStudio.put("user_id",
								currentRow.getInt("user_id"));
						currentStudio.put("contact_name",
								currentRow.getString("contact_name"));
						currentStudio.put("email",
								currentRow.getString("email"));
						currentStudio.put("phone",
								currentRow.getString("phone"));
						currentStudio.put("logo",
								currentRow.getString("logo_url"));

						// Add avg rating
						JSONArray avg_rating = MainDBHandler
								.selectWithParameters(
										"select avg(RATING) as avg_rating "
												+ "from REVIEWS "
												+ "where STUDIO_ID = ?",
										studioID);

						if (avg_rating.getJSONObject(0).has("avg_rating")) {
							// Add avg
							currentStudio.put("avg_rating", avg_rating
									.getJSONObject(0).getDouble("avg_rating"));
						} else {
							// no avg
							currentStudio.put("avg_rating", "no reviews");
						}

						arrayResult.put(currentStudio);
					}
					// the studio exists in the result
					else {
						currentStudio = arrayResult.getJSONObject(studioIndex);
					}

					// Add the room
					JSONObject room = new JSONObject();
					room.put("id", currentRow.getInt("room_id"));
					room.put("rate", currentRow.getInt("rate"));
					room.put("name", currentRow.getString("room_name"));
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
		}finally {
			RedisManager.returnResource(redisConn);
		}
	}

	@GET
	@Path("/mostviewed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMostViewed() {
		Jedis redisConn = RedisManager.getConnection();
		Set<Tuple> elements = redisConn
				.zrangeWithScores("MOST_VIEWED", -10, -1);
		RedisManager.returnResource(redisConn);

		JSONArray mostViewedArray = new JSONArray();

		for (Tuple tuple : elements) {
			JSONObject studio = new JSONObject(tuple.getElement());
			int score = (int) tuple.getScore();
			studio.put("score", score);

			mostViewedArray.put(studio);
		}

		return Response.ok(
				mostViewedArray.toString(SPACE_TO_INDENTS_EACH_LEVEL)).build();
	}

	@GET
	@Path("/recent")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMostRecent() {

		Jedis redisConn = RedisManager.getConnection();
		List<String> studioStrings = redisConn.lrange("NEW_STUDIOS", 0, 9);
		RedisManager.returnResource(redisConn);

		JSONArray recentStudios = new JSONArray();

		for (String studioString : studioStrings) {
			JSONObject studioObject = new JSONObject(studioString);
			recentStudios.put(studioObject);
		}

		return Response.ok(recentStudios.toString(SPACE_TO_INDENTS_EACH_LEVEL))
				.build();
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
