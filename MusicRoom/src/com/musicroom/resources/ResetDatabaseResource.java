/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musicroom.resources;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.musicroom.database.MainDBHandler;
import com.musicroom.database.initialization.MusicRoomSchemaCreator;
import com.musicroom.database.interfaces.MySQLDataBasePopulator;
import com.musicroom.database.interfaces.MySQLSchemaCreator;

@Path("/ResetDatabase")
public class ResetDatabaseResource {
	private static final String RESET_MYSQL_DB_USERNAME = "root";
	private static final String RESET_MYSQL_DB_PASSWORD = "root";
	private static final String INVALID_USER_OR_PASSWORD_RESPONSE_STRING = "Invalid user or password!";
	private static final String SUCCESS_RESPONSE_STRING = "Success!";
	private static final String FAILED_RESPONSE_STRING = "Reset failed!";

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String resetDB(
			@QueryParam("user") @DefaultValue("-") String username,
			@QueryParam("password") String password) {
		try {
			if (RESET_MYSQL_DB_USERNAME.equals(username)
					&& RESET_MYSQL_DB_PASSWORD.equals(password)) {
				createAndFillDB();
				return SUCCESS_RESPONSE_STRING;
			} else {
				return INVALID_USER_OR_PASSWORD_RESPONSE_STRING;
			}
		} catch (Exception e) {
			return FAILED_RESPONSE_STRING;
		}
	}

	private void createAndFillDB() {
		Connection con = MainDBHandler.getConnection();
		try {

			MySQLSchemaCreator dbCreator = new MusicRoomSchemaCreator();
			dbCreator.createDB(con);

			MySQLDataBasePopulator dbPopulator = dbCreator.getPopulator();
			dbPopulator.initDBData(con);

			System.out.println("Create database scheme");
		} catch (SQLException ex) {
			Logger.getLogger(ResetDatabaseResource.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}
}
