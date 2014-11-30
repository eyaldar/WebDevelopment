package com.musicroom.database;

import java.sql.*;
import org.json.*;

public class MainDBHandler 
{

	private static Connection connection = null;
	
	public static void CloseConnection()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
                System.out.println("Database connection terminated");
            }
            catch (Exception e)
            {
                System.err.println("Errors closing the DB connection");
            }
        }
    }
	
	public static Connection GetConnection()
    {		
        if (connection == null)
        {
            try
            {
                String userName = "root";
                String password = "root";
                String url = "jdbc:mysql://localhost/musicRoomDB";

                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(url, userName, password);

                System.out.println("Connected to database");
            }
            catch (Exception e)
            {
                System.err.println("Cannot connect to database server");
                System.err.println(e.getMessage());
            }
        }

        return (connection);
    }
	
	public static void createDB(Connection connection) throws SQLException
    {
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
        stmt.executeUpdate("CREATE TABLE AREAS(" +
                           "       ID 			INTEGER UNSIGNED PRIMARY KEY," +
                           "	   AREA_NAME 	VARCHAR(50) NOT NULL" + 
                           ");");
        
        // Cities
        System.out.println("Creating table CITIES");
        stmt.executeUpdate("DROP TABLE IF EXISTS CITIES;");
        stmt.executeUpdate("CREATE TABLE CITIES(" +
                           "	    ID           INTEGER UNSIGNED PRIMARY KEY," +
                           "	    CITIE_NAME   VARCHAR(100) NOT NULL," +
                           "        AREA_ID      INTEGER UNSIGNED NOT NULL," +
                           "	    Foreign Key (AREA_ID) references AREAS(ID)" +
                           ");");
        
        // User types
        System.out.println("Creating table USER_TYPES");
        stmt.executeUpdate("DROP TABLE IF EXISTS USER_TYPES;");
        stmt.executeUpdate("CREATE TABLE USER_TYPES(" +
                           "       ID 			INTEGER UNSIGNED PRIMARY KEY," +
                           "	   DESCRIPTION 	VARCHAR(50) NOT NULL" + 
                           ");");
        
        // Users
        System.out.println("Creating table USERS");
        stmt.executeUpdate("DROP TABLE IF EXISTS USERS;");
        stmt.executeUpdate("CREATE TABLE USERS(" +
                           "       ID 			INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
                           "	   USER_NAME 	VARCHAR(100) NOT NULL," + 
                           "	   PASSWORD 	VARCHAR(100) NOT NULL," +
                           "	   USER_TYPE_ID	INTEGER UNSIGNED NOT NULL," + 
                           "	   Foreign Key (USER_TYPE) references USER_TYPES(ID)," +
                           "	   UNIQUE(USER_NAME)" + 
                           ");");
        
        // Room types
        System.out.println("Creating table ROOM_TYPES");
        stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_TYPES;");
        stmt.executeUpdate("CREATE TABLE ROOM_TYPES(" +
                           "       ID 				INTEGER UNSIGNED PRIMARY KEY," +
                           "	   ROOM_TYPE_NAME 	VARCHAR(100) NOT NULL" + 
                           ");");
        
        // EQUIPMENT CATEGORIES
        System.out.println("Creating table EQUIPMENT_CATEGORIES");
        stmt.executeUpdate("DROP TABLE IF EXISTS EQUIPMENT_CATEGORIES;");
        stmt.executeUpdate("CREATE TABLE EQUIPMENT_CATEGORIES(" +
                           "       ID 				INTEGER UNSIGNED PRIMARY KEY," +
                           "	   EQUIP_CAT_NAME 	VARCHAR(100) NOT NULL" + 
                           ");");
        
        // EQUIPMENT TYPES
        System.out.println("Creating table EQUIPMENT_TYPES");
        stmt.executeUpdate("DROP TABLE IF EXISTS EQUIPMENT_TYPES;");
        stmt.executeUpdate("CREATE TABLE EQUIPMENT_TYPES(" +
                           "       ID 				INTEGER UNSIGNED PRIMARY KEY," +
                           "	   EQUIPMENT_NAME 	VARCHAR(100) NOT NULL," +
                           "	   CATEGORY_ID 		INTEGER UNSIGNED NOT NULL," +
                           "	   Foreign Key (CATEGORY_ID) references EQUIPMENT_CATEGORIES(ID)" +
                           ");");
        
        // Studios
        System.out.println("Creating table STUDIOS");
        stmt.executeUpdate("DROP TABLE IF EXISTS STUDIOS;");
        stmt.executeUpdate("CREATE TABLE STUDIOS(" +
                           "       ID 				INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
                           "	   STUDIO_NAME	 	VARCHAR(100) NOT NULL," +
                           "	   CITY_ID	 		INTEGER UNSIGNED NOT NULL," +
                           "	   ADDRESS	 		VARCHAR(200) NOT NULL," +
                           "	   EMAIL	 		VARCHAR(100) NOT NULL," +
                           "	   CONTACT_NAME		VARCHAR(100) NOT NULL," +
                           "	   SITE_URL			VARCHAR(100) NOT NULL," +
                           "	   FACEBOOK_PAGE	VARCHAR(100) NOT NULL," +
                           "	   PHONE			VARCHAR(15) NOT NULL," +
                           "	   USER_ID			INTEGER UNSIGNED NOT NULL," +
                           "	   EXTRA_DETAILS	VARCHAR(200) NOT NULL," +
                           "	   LOGO_URL			VARCHAR(100) NOT NULL," +
                           "	   Foreign Key (CITY_ID) references CITIES(ID)," +
                           "	   Foreign Key (USER_ID) references USERS(ID)" +
                           ");");
        
        // Rooms
        System.out.println("Creating table ROOMS");
        stmt.executeUpdate("DROP TABLE IF EXISTS ROOMS;");
        stmt.executeUpdate("CREATE TABLE ROOMS(" +
                           "       ID 				INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
                           "	   STUDIO_ID	 	INTEGER UNSIGNED NOT NULL," +
                           "	   RATE		 		INTEGER UNSIGNED NOT NULL," +
                           "	   ROOM_NAME 		VARCHAR(100) NOT NULL," +
                           "	   EXTRA_DETAILS	VARCHAR(200) NOT NULL," +
                           "	   Foreign Key (STUDIO_ID) references STUDIOS(ID)" +
                           ");");
        
        // Rooms to room types
        System.out.println("Creating table ROOM_ROOM_TYPES");
        stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_ROOM_TYPES;");
        stmt.executeUpdate("CREATE TABLE ROOM_ROOM_TYPES(" +
                           "       ROOM_ID		INTEGER UNSIGNED NOT NULL," +
                           "	   TYPE_ID	 	INTEGER UNSIGNED NOT NULL," +
                           "	   PRIMARY KEY (ROOM_ID, TYPE_ID)," +
                           "	   Foreign Key (ROOM_ID) references ROOMS(ID)," +
                           "	   Foreign Key (TYPE_ID) references ROOM_TYPES(ID)" +
                           ");");
        
        // reviews
        System.out.println("Creating table REVIEWS");
        stmt.executeUpdate("DROP TABLE IF EXISTS REVIEWS;");
        stmt.executeUpdate("CREATE TABLE REVIEWS(" +
                           "       ID			INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
                           "	   STUDIO_ID 	INTEGER UNSIGNED NOT NULL," +
                           "	   RATING	 	INTEGER UNSIGNED NOT NULL," +
                           "	   COMMENT	 	VARCHAR(200) NOT NULL," +
                           "	   USER_ID	 	INTEGER UNSIGNED NOT NULL," +
                           "	   Foreign Key (STUDIO_ID) references STUDIOS(ID)," +
                           "	   Foreign Key (USER_ID) references USERS(ID)" +
                           ");");
        
        // bands
        System.out.println("Creating table BANDS");
        stmt.executeUpdate("DROP TABLE IF EXISTS BANDS;");
        stmt.executeUpdate("CREATE TABLE BANDS(" +
                           "       ID			INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
                           "	   BAND_NAME 	VARCHAR(100) NOT NULL," +
                           "	   LOGO_URL	 	VARCHAR(100) NOT NULL," +
                           "	   GENRE	 	VARCHAR(50) NOT NULL," +
                           "	   USER_ID	 	INTEGER UNSIGNED NOT NULL," +
                           "	   Foreign Key (USER_ID) references USERS(ID)" +
                           ");");
        
        // band members
        System.out.println("Creating table BAND_MEMBERS");
        stmt.executeUpdate("DROP TABLE IF EXISTS BAND_MEMBERS;");
        stmt.executeUpdate("CREATE TABLE BAND_MEMBERS(" +
                           "       ID			INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
                           "	   MEMBER_NAME 	VARCHAR(100) NOT NULL," +
                           "	   ROLE		 	VARCHAR(100) NOT NULL," +
                           "	   PICTURE_URL 	VARCHAR(100) NOT NULL," +
                           "	   BAND_ID	 	INTEGER UNSIGNED NOT NULL," +
                           "	   Foreign Key (BAND_ID) references BAND_MEMBERS(ID)" +
                           ");");
        
        // members instruments
        System.out.println("Creating table MEMBER_INSTRUMENT");
        stmt.executeUpdate("DROP TABLE IF EXISTS MEMBER_INSTRUMENT;");
        stmt.executeUpdate("CREATE TABLE MEMBER_INSTRUMENT(" +
                           "       MEMBER_ID			INTEGER UNSIGNED NOT NULL," +
                           "	   EQUIPMENT_TYPE_ID 	INTEGER UNSIGNED NOT NULL," +
                           "	   PRIMARY KEY (MEMBER_ID, EQUIPMENT_TYPE_ID)," +
                           "	   Foreign Key (EQUIPMENT_TYPE_ID) references EQUIPMENT_TYPES(ID)," +
                           "	   Foreign Key (MEMBER_ID) references BAND_MEMBERS(ID)" +
                           ");");
        
        // Room Equipment
        System.out.println("Creating table ROOM_EQUIPMENT");
        stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_EQUIPMENT;");
        stmt.executeUpdate("CREATE TABLE ROOM_EQUIPMENT(" +
                           "       ROOM_ID				INTEGER UNSIGNED NOT NULL," +
                           "	   EQUIPMENT_TYPE_ID 	INTEGER UNSIGNED NOT NULL," +
                           "	   MODEL	 			VARCHAR(100) NOT NULL," +
                           "	   MANUFACTURER			VARCHAR(100) NOT NULL," +
                           "	   QUANTITY	 			INTEGER UNSIGNED NOT NULL," +
                           "	   PRIMARY KEY (ROOM_ID, EQUIPMENT_TYPE_ID)," +
                           "	   Foreign Key (EQUIPMENT_TYPE_ID) references EQUIPMENT_TYPES(ID)," +
                           "	   Foreign Key (ROOM_ID) references ROOMS(ID)" +
                           ");");
        
        // Room schedule
        System.out.println("Creating table ROOM_SCHEDULE");
        stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_SCHEDULE;");
        stmt.executeUpdate("CREATE TABLE ROOM_SCHEDULE(" +
                           "       ROOM_ID				INTEGER UNSIGNED NOT NULL," +
                           "	   BAND_ID			 	INTEGER UNSIGNED NOT NULL," +
                           "	   START_TIME 			DATETIME NOT NULL," +
                           "	   END_TIME				DATETIME NOT NULL," +
                           "	   PRIMARY KEY (ROOM_ID, START_TIME, END_TIME)," +
                           "	   Foreign Key (BAND_ID) references BANDS(ID)," +
                           "	   Foreign Key (ROOM_ID) references ROOMS(ID)" +
                           ");");
    }
	
	public static void InitDBData(Connection connection) throws SQLException
    {
        try
        {
            Statement stmt = connection.createStatement();

            System.out.println("-----------------");

            System.out.println("Adding Areas");
            stmt.executeUpdate("INSERT INTO AREAS VALUES(1, 'Center')");
            stmt.executeUpdate("INSERT INTO AREAS VALUES(2, 'North')");
            stmt.executeUpdate("INSERT INTO AREAS VALUES(3, 'South')");
            stmt.executeUpdate("INSERT INTO AREAS VALUES(4, 'Jerusalem')");
            stmt.executeUpdate("INSERT INTO AREAS VALUES(5, 'HaSharon')");
            stmt.executeUpdate("INSERT INTO AREAS VALUES(6, 'HaShfela')");
        }
        catch (SQLException e)
        {
            System.err.println("error initializing the DB");
        }
    }

    public static String getAreas()
    {
        JSONObject result = new JSONObject();
        
        String selectionString = "select * from AREAS";
        
        try
        {
            // Create a result set containing all data from my_table
            Statement stmt = GetConnection().createStatement();

            // Executed query
            ResultSet rs = stmt.executeQuery(selectionString);

            // Fetch each row from the result set
            while (rs.next())
            {
            	JSONObject current = new JSONObject();
            	current.put("ID", rs.getInt("ID"));
            	current.put("AREA_NAME", rs.getInt("AREA_NAME"));
            	
            	result.append("AREAS", current);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in reading data");
        }

        return (result.toString());
    }
}
