package com.musicroom.database.initialization;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.musicroom.database.interfaces.MySQLDataBasePopulator;
import com.musicroom.database.interfaces.MySQLSchemaCreator;

public class MusicRoomSchemaCreator implements MySQLSchemaCreator {
	public void createDB(Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();

		// Dropping (if exists) and creating a new musicRoomDB
		System.out.println("Creating DB");
		stmt.executeUpdate("drop database if exists musicRoomDB;");
		stmt.executeUpdate("create database if not exists musicRoomDB character set = utf8 collate = utf8_unicode_ci;");
		stmt.executeUpdate("USE musicRoomDB");

		// Creating tables

		// Areas
		System.out.println("Creating table AREAS");
		stmt.executeUpdate("DROP TABLE IF EXISTS AREAS;");
		stmt.executeUpdate("CREATE TABLE AREAS("
				+ "       ID 			INTEGER UNSIGNED PRIMARY KEY,"
				+ "	   AREA_NAME 	VARCHAR(50) NOT NULL" + ");");

		// Cities
		System.out.println("Creating table CITIES");
		stmt.executeUpdate("DROP TABLE IF EXISTS CITIES;");
		stmt.executeUpdate("CREATE TABLE CITIES("
				+ "	    ID           INTEGER UNSIGNED PRIMARY KEY,"
				+ "	    CITY_NAME   VARCHAR(100) NOT NULL,"
				+ "        AREA_ID      INTEGER UNSIGNED NOT NULL,"
				+ "	    Foreign Key (AREA_ID) references AREAS(ID)" + ");");

		// User types
		System.out.println("Creating table USER_TYPES");
		stmt.executeUpdate("DROP TABLE IF EXISTS USER_TYPES;");
		stmt.executeUpdate("CREATE TABLE USER_TYPES("
				+ "       ID 			INTEGER UNSIGNED PRIMARY KEY,"
				+ "	   DESCRIPTION 	VARCHAR(50) NOT NULL" + ");");

		// Users
		System.out.println("Creating table USERS");
		stmt.executeUpdate("DROP TABLE IF EXISTS USERS;");
		stmt.executeUpdate("CREATE TABLE USERS("
				+ "       ID 			INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,"
				+ "	   USER_NAME 	VARCHAR(100) NOT NULL,"
				+ "	   PASSWORD 	VARCHAR(100) NOT NULL,"
				+ "	   USER_TYPE_ID	INTEGER UNSIGNED NOT NULL,"
				+ "	   Foreign Key (USER_TYPE_ID) references USER_TYPES(ID),"
				+ "	   UNIQUE(USER_NAME)" + ");");

		// Room types
		System.out.println("Creating table ROOM_TYPES");
		stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_TYPES;");
		stmt.executeUpdate("CREATE TABLE ROOM_TYPES("
				+ "       ID 				INTEGER UNSIGNED PRIMARY KEY,"
				+ "	   ROOM_TYPE_NAME 	VARCHAR(100) NOT NULL" + ");");

		// EQUIPMENT CATEGORIES
		System.out.println("Creating table EQUIPMENT_CATEGORIES");
		stmt.executeUpdate("DROP TABLE IF EXISTS EQUIPMENT_CATEGORIES;");
		stmt.executeUpdate("CREATE TABLE EQUIPMENT_CATEGORIES("
				+ "       ID 				INTEGER UNSIGNED PRIMARY KEY,"
				+ "	   EQUIP_CAT_NAME 	VARCHAR(100) NOT NULL" + ");");

		// EQUIPMENT TYPES
		System.out.println("Creating table EQUIPMENT_TYPES");
		stmt.executeUpdate("DROP TABLE IF EXISTS EQUIPMENT_TYPES;");
		stmt.executeUpdate("CREATE TABLE EQUIPMENT_TYPES("
				+ "       ID 				INTEGER UNSIGNED PRIMARY KEY,"
				+ "	   EQUIPMENT_NAME 	VARCHAR(100) NOT NULL,"
				+ "	   CATEGORY_ID 		INTEGER UNSIGNED NOT NULL,"
				+ "	   Foreign Key (CATEGORY_ID) references EQUIPMENT_CATEGORIES(ID)"
				+ ");");

		// Studios
		System.out.println("Creating table STUDIOS");
		stmt.executeUpdate("DROP TABLE IF EXISTS STUDIOS;");
		stmt.executeUpdate("CREATE TABLE STUDIOS("
				+ "       ID 				INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,"
				+ "	   STUDIO_NAME	 	VARCHAR(100) NOT NULL,"
				+ "	   CITY_ID	 		INTEGER UNSIGNED NOT NULL,"
				+ "	   ADDRESS	 		VARCHAR(200) NOT NULL,"
				+ "	   EMAIL	 		VARCHAR(100) NOT NULL,"
				+ "	   CONTACT_NAME		VARCHAR(100) NOT NULL,"
				+ "	   SITE_URL			VARCHAR(500) NOT NULL,"
				+ "	   FACEBOOK_PAGE	VARCHAR(500) NOT NULL,"
				+ "	   PHONE			VARCHAR(15) NOT NULL,"
				+ "	   USER_ID			INTEGER UNSIGNED NOT NULL,"
				+ "	   EXTRA_DETAILS	VARCHAR(200) NOT NULL,"
				+ "	   LOGO_URL			VARCHAR(500) NOT NULL,"
				+ "	   VOTES_COUNT		INTEGER UNSIGNED NOT NULL,"
				+ "	   VOTES_SUM		INTEGER UNSIGNED NOT NULL,"
				+ "	   Foreign Key (CITY_ID) references CITIES(ID),"
				+ "	   Foreign Key (USER_ID) references USERS(ID)" + ");");

		// Rooms
		System.out.println("Creating table ROOMS");
		stmt.executeUpdate("DROP TABLE IF EXISTS ROOMS;");
		stmt.executeUpdate("CREATE TABLE ROOMS("
				+ "       ID 				INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,"
				+ "	   STUDIO_ID	 	INTEGER UNSIGNED NOT NULL,"
				+ "	   RATE		 		INTEGER UNSIGNED NOT NULL,"
				+ "	   ROOM_NAME 		VARCHAR(100) NOT NULL,"
				+ "	   EXTRA_DETAILS	VARCHAR(200) NOT NULL,"
				+ "	   Foreign Key (STUDIO_ID) references STUDIOS(ID)" + ");");

		// Rooms to room types
		System.out.println("Creating table ROOM_ROOM_TYPES");
		stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_ROOM_TYPES;");
		stmt.executeUpdate("CREATE TABLE ROOM_ROOM_TYPES("
				+ "       ROOM_ID		INTEGER UNSIGNED NOT NULL,"
				+ "	   TYPE_ID	 	INTEGER UNSIGNED NOT NULL,"
				+ "	   PRIMARY KEY (ROOM_ID, TYPE_ID),"
				+ "	   Foreign Key (ROOM_ID) references ROOMS(ID),"
				+ "	   Foreign Key (TYPE_ID) references ROOM_TYPES(ID)" + ");");

		// reviews
		System.out.println("Creating table REVIEWS");
		stmt.executeUpdate("DROP TABLE IF EXISTS REVIEWS;");
		stmt.executeUpdate("CREATE TABLE REVIEWS("
				+ "       ID			INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,"
				+ "	   STUDIO_ID 	INTEGER UNSIGNED NOT NULL,"
				+ "	   RATING	 	INTEGER UNSIGNED NOT NULL,"
				+ "	   COMMENT	 	VARCHAR(200) NOT NULL,"
				+ "	   USER_ID	 	INTEGER UNSIGNED NOT NULL,"
				+ "	   Foreign Key (STUDIO_ID) references STUDIOS(ID),"
				+ "	   Foreign Key (USER_ID) references USERS(ID)" + ");");

		// bands
		System.out.println("Creating table BANDS");
		stmt.executeUpdate("DROP TABLE IF EXISTS BANDS;");
		stmt.executeUpdate("CREATE TABLE BANDS("
				+ "       ID			INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,"
				+ "	   BAND_NAME 	VARCHAR(100) NOT NULL,"
				+ "	   LOGO_URL	 	VARCHAR(500) NOT NULL,"
				+ "	   GENRE	 	VARCHAR(50) NOT NULL,"
				+ "	   USER_ID	 	INTEGER UNSIGNED NOT NULL,"
				+ "	   Foreign Key (USER_ID) references USERS(ID)" + ");");

		// band members
		System.out.println("Creating table BAND_MEMBERS");
		stmt.executeUpdate("DROP TABLE IF EXISTS BAND_MEMBERS;");
		stmt.executeUpdate("CREATE TABLE BAND_MEMBERS("
				+ "       ID			INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,"
				+ "	   MEMBER_NAME 	VARCHAR(100) NOT NULL,"
				+ "	   ROLE		 	VARCHAR(100) NOT NULL,"
				+ "	   PICTURE_URL 	VARCHAR(500) NOT NULL,"
				+ "	   BAND_ID	 	INTEGER UNSIGNED NOT NULL,"
				+ "	   Foreign Key (BAND_ID) references BAND_MEMBERS(ID)"
				+ ");");

		// members instruments
		System.out.println("Creating table MEMBER_INSTRUMENT");
		stmt.executeUpdate("DROP TABLE IF EXISTS MEMBER_INSTRUMENT;");
		stmt.executeUpdate("CREATE TABLE MEMBER_INSTRUMENT("
				+ "       MEMBER_ID			INTEGER UNSIGNED NOT NULL,"
				+ "	   EQUIPMENT_TYPE_ID 	INTEGER UNSIGNED NOT NULL,"
				+ "	   PRIMARY KEY (MEMBER_ID, EQUIPMENT_TYPE_ID),"
				+ "	   Foreign Key (EQUIPMENT_TYPE_ID) references EQUIPMENT_TYPES(ID),"
				+ "	   Foreign Key (MEMBER_ID) references BAND_MEMBERS(ID)"
				+ ");");

		// Room Equipment
		System.out.println("Creating table ROOM_EQUIPMENT");
		stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_EQUIPMENT;");
		stmt.executeUpdate("CREATE TABLE ROOM_EQUIPMENT("
				+ "       ROOM_ID				INTEGER UNSIGNED NOT NULL,"
				+ "	   EQUIPMENT_TYPE_ID 	INTEGER UNSIGNED NOT NULL,"
				+ "	   MODEL	 			VARCHAR(100) NOT NULL,"
				+ "	   MANUFACTURER			VARCHAR(100) NOT NULL,"
				+ "	   QUANTITY	 			INTEGER UNSIGNED NOT NULL,"
				+ "	   PRIMARY KEY (ROOM_ID, EQUIPMENT_TYPE_ID),"
				+ "	   Foreign Key (EQUIPMENT_TYPE_ID) references EQUIPMENT_TYPES(ID),"
				+ "	   Foreign Key (ROOM_ID) references ROOMS(ID)" + ");");

		// Room schedule
		System.out.println("Creating table ROOM_SCHEDULE");
		stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_SCHEDULE;");
		stmt.executeUpdate("CREATE TABLE ROOM_SCHEDULE("
				+ "       ROOM_ID				INTEGER UNSIGNED NOT NULL,"
				+ "	   BAND_ID			 	INTEGER UNSIGNED NOT NULL,"
				+ "	   START_TIME 			DATETIME NOT NULL,"
				+ "	   END_TIME				DATETIME NOT NULL,"
				+ "	   PRIMARY KEY (ROOM_ID, START_TIME, END_TIME),"
				+ "	   Foreign Key (BAND_ID) references BANDS(ID),"
				+ "	   Foreign Key (ROOM_ID) references ROOMS(ID)" + ");");

		// Create Indexes
		System.out.println("Creating table indexes");
		stmt.executeUpdate("CREATE INDEX STD_CT_IDX 		ON STUDIOS 			(CITY_ID)");
		stmt.executeUpdate("CREATE INDEX CT_AREA_IDX 	ON CITIES 			(AREA_ID)");
		stmt.executeUpdate("CREATE INDEX STD_NAME_IDX 	ON STUDIOS 			(STUDIO_NAME)");
		stmt.executeUpdate("CREATE INDEX ROM_STD_IDX 	ON ROOMS 			(STUDIO_ID)");
		stmt.executeUpdate("CREATE INDEX ROM_RATE_IDX 	ON ROOMS 			(RATE)");
		stmt.executeUpdate("CREATE INDEX EQP_CAT_IDX 	ON EQUIPMENT_TYPES 	(CATEGORY_ID)");
		stmt.executeUpdate("CREATE INDEX USR_PAS_IDX 	ON USERS 			(PASSWORD)");
		stmt.executeUpdate("CREATE UNIQUE INDEX USR_NAME_IDX 	ON USERS 			(USER_NAME)");
	}

	@Override
	public MySQLDataBasePopulator getPopulator() {
		return new MusicRoomSampleDataPopulator();
	}
}
