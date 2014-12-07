package com.musicroom.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.utils.JSONUtils;

public class MainDBHandler {
	private static Connection connection = null;

	public static void CloseConnection() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Database connection terminated");
			} catch (Exception e) {
				System.err.println("Errors closing the DB connection");
			}
		}
	}

	public static Connection GetConnection() {
		if (connection == null) {
			try {
				String userName = "root";
				String password = "";
				String url = "jdbc:mysql://localhost/";

				Class.forName("com.mysql.jdbc.Driver").newInstance();
				connection = DriverManager.getConnection(url, userName,
						password);

				System.out.println("Connected to database");
			} catch (Exception e) {
				System.err.println("Cannot connect to database server");
				System.err.println(e.getMessage());
			}
		}

		return (connection);
	}

	public static JSONArray getAreas() {
		JSONArray result = getStatementResult("select * from AREAS");

		return result;
	}

	public static JSONArray getCities() {
		JSONArray result = getStatementResult("select * from CITIES");

		return result;
	}

	public static JSONArray getUserTypes() {
		JSONArray result = getStatementResult("select * from USER_TYPES");

		return result;
	}

	public static JSONArray getEquipmentCategories() {
		JSONArray result = getStatementResult("select * from EQUIPMENT_CATEGORIES");

		return result;
	}

	public static JSONArray getEquipmentTypes() {
		JSONArray result = getStatementResult("select * from EQUIPMENT_TYPES");

		return result;
	}

	public static JSONArray getRoomTypes() {
		JSONArray result = getStatementResult("select * from ROOM_TYPES");

		return result;
	}

	public static String getUserByLogin(JSONObject loginData) {
		String selectionString = "select * from USERS where USER_NAME = ? and PASSWORD = ?";
		JSONArray results = getPreparedStatementResult(selectionString,
				loginData.getString("USER_NAME"),
				loginData.getString("PASSWORD"));
		JSONObject result = JSONUtils.extractJSONObject(results);

		return result.toString();
	}

	public static JSONArray getStatementResult(String selectionString) {
		JSONArray result = new JSONArray();

		try {
			Statement stmt = GetConnection().createStatement();
			stmt.executeUpdate("USE musicRoomDB");

			// Executed query
			ResultSet rs = stmt.executeQuery(selectionString);

			result = JSONUtils.convertToJSON(rs);
		} catch (Exception e) {
			System.err.println("Error in reading data");
		}

		return result;
	}
	
	public static JSONArray getPreparedStatementResult(String selectionString,
			Object... params) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = GetConnection().prepareStatement(
					selectionString);
			stmt.executeUpdate("USE musicRoomDB");

			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);
			}

			// Executed query
			ResultSet rs = stmt.executeQuery();

			result = JSONUtils.convertToJSON(rs);
		} catch (Exception e) {
			System.err.println("Error in reading data");
		}

		return result;
	}
}
