package com.musicroom.rest.api;

import java.sql.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.database.MainDBHandler;
import com.musicroom.utils.JSONUtils;

@Path("/studios")
public class StudiosRequestsAPI {

	private static final String STUDIO_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Studio with id '%d' was not found\"}";
	private static final String USER_ALREADY_EXISTS_ERROR_JSON = "{\"error\":\"User name '%s' already exists\"}";
	
	@GET
	@Produces("application/json")
	public Response getStudios() 
	{
		JSONArray results = 
				MainDBHandler.getStatementResult("select * from STUDIOS");

		return Response.ok(results.toString()).build();
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response getStudioByID(@PathParam("id") int id)
	{
		JSONArray results = 
				MainDBHandler.getPreparedStatementResult("select * from STUDIOS where id = ?", id);

		JSONObject result = JSONUtils.extractJSONObject(results);

		if (result.length() > 0) 
		{
			return Response.ok(result.toString()).build();
		} 
		else 
		{
			String errorJson = String.format(STUDIO_ID_WAS_NOT_FOUND_ERROR_JSON, id);
			return Response.status(HttpServletResponse.SC_NOT_FOUND).entity(errorJson).build();
		}
	}
	
	public Response AddStudio(JSONObject data)
	{
		JSONObject userObj = data.getJSONObject("user");

		JSONArray existingUser = MainDBHandler.getPreparedStatementResult("select * from USERS where USER_NAME = ?", userObj.getString("name"));
		
		// if there are results from the user check
		if (existingUser.length() > 0)
		{
			String errorJson = String.format(USER_ALREADY_EXISTS_ERROR_JSON, userObj.getString("name"));
			return Response.status(HttpServletResponse.SC_CONFLICT).entity(errorJson).build();
		}
		else
		{
			try 
			{
				Connection conn = MainDBHandler.GetConnection();
				conn.setAutoCommit(false);
				
				// Add the user
				PreparedStatement stmt = 
						conn.prepareStatement("INSERT INTO USERS (USER_NAME, PASSWORD, USER_TYPE_ID) VALUES(?, ?, 1)",
																		Statement.RETURN_GENERATED_KEYS);
				
				stmt.executeUpdate("USE musicRoomDB");
				stmt.setString(1, userObj.getString("name"));
				stmt.setString(2, userObj.getString("password"));
				stmt.executeUpdate();
				
				ResultSet rs = stmt.getGeneratedKeys();
				rs.next();
				int userID = rs.getInt(1);
				
				// Add studio
				JSONObject studioObj = data.getJSONObject("studio");
				
				stmt = conn.prepareStatement(
								  "INSERT INTO STUDIOS (STUDIO_NAME, CITY_ID, ADDRESS, EMAIL, CONTACT_NAME, PHONE, USER_ID, EXTRA_DETAILS, "
								+ " FACEBOOK_PAGE, SITE_URL, LOGO_URL) "
								+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
								Statement.RETURN_GENERATED_KEYS);
				
				stmt.executeUpdate("USE musicRoomDB");
				stmt.setString(1, studioObj.getString("name"));
				stmt.setInt(2, studioObj.getInt("city_id"));
				stmt.setString(3, studioObj.getString("address"));
				stmt.setString(4, studioObj.getString("email"));
				stmt.setString(5, studioObj.getString("contact_name"));
				stmt.setString(6, studioObj.getString("phone"));
				stmt.setInt(7, userID);
				stmt.setString(8, studioObj.getString("extra_details"));
				stmt.setString(9, studioObj.getString("facebook_page"));
				stmt.setString(10, studioObj.getString("site"));
				stmt.setString(11, studioObj.getString("logo"));
				
				stmt.executeUpdate();
				
				rs = stmt.getGeneratedKeys();
				rs.next();
				int studioID = rs.getInt(1);
				
				// Add rooms
				JSONArray roomsArray = studioObj.getJSONArray("rooms");
				
				for (int i = 0; i < roomsArray.length(); i++)
				{
					// Add room
					JSONObject roomObj = roomsArray.getJSONObject(i);
					
					stmt = conn.prepareStatement(
										"INSERT INTO ROOMS (STUDIO_ID, RATE, ROOM_NAME, EXTRA_DETAILS) VALUES(?, ?, ?, ?)",
										Statement.RETURN_GENERATED_KEYS);
					
					stmt.executeUpdate("USE musicRoomDB");
					stmt.setInt(1, studioID);
					stmt.setInt(2, roomObj.getInt("rate"));
					stmt.setString(3, roomObj.getString("name"));
					stmt.setString(4, roomObj.getString("extra_details"));
					
					stmt.executeUpdate();
					
					rs = stmt.getGeneratedKeys();
					rs.next();
					int roomID = rs.getInt(1);
					
					// Add room types
					JSONArray roomsTypesArray = roomObj.getJSONArray("room_type");
					
					for (int k = 0; k < roomsTypesArray.length(); k++)
					{
						stmt = conn.prepareStatement("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) VALUES (?, ?)");
						stmt.executeUpdate("USE musicRoomDB");
						stmt.setInt(1, roomID);
						stmt.setInt(2, roomsTypesArray.getInt(k));
						stmt.executeUpdate();
					}
					
					// Add room equipments
					JSONArray roomsEquipmentArray = roomObj.getJSONArray("equipment");
					
					for (int k = 0; k < roomsEquipmentArray.length(); k++)
					{
						JSONObject equipObj = roomsEquipmentArray.getJSONObject(k);
						
						stmt = conn.prepareStatement(
								"INSERT INTO ROOM_EQUIPMENT (ROOM_ID, EQUIPMENT_TYPE_ID, MODEL, MANUFACTURER, QUANTITY) VALUES(?,?,?,?,?)");
						stmt.executeUpdate("USE musicRoomDB");
						stmt.setInt(1, roomID);
						stmt.setInt(2, equipObj.getInt("type"));
						stmt.setString(3, equipObj.getString("model"));
						stmt.setString(4, equipObj.getString("manufacturer"));
						stmt.setInt(5, equipObj.getInt("quantity"));
						stmt.executeUpdate();
					}
				}
				
				MainDBHandler.GetConnection().commit();
				MainDBHandler.GetConnection().setAutoCommit(true);
				
				return Response.ok("{message: \"success\"}").build();
			} 
			catch (SQLException e) 
			{
				return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}
