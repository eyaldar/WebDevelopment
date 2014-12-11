package com.musicroom.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;

import com.musicroom.database.MainDBHandler;

public class UsersTableUtils {

	public static final String USER_ALREADY_EXISTS_ERROR_JSON = "{\"error\":\"User name '%s' already exists\"}";
	
	public static int addUser(String username, String password,
			UserType userType) throws SQLException {
		ResultSet rs = MainDBHandler
				.insertWithAutoKey(
						"INSERT INTO USERS (USER_NAME, PASSWORD, USER_TYPE_ID) VALUES(?, ?, " + userType.toInt() + ")",
						PreparedStatement.RETURN_GENERATED_KEYS, username,
						password);
		if (!rs.next()) {
			return -1;
		}

		return rs.getInt(1);
	}

	public static boolean isExistingUser(String name) throws Exception {
		JSONArray existingUser = MainDBHandler.selectWithParameters(
				"select * from USERS where USER_NAME = ?", name);

		return existingUser.length() > 0;
	}
}
