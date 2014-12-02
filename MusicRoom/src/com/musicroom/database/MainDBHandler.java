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
                String password = "";
                String url = "jdbc:mysql://localhost/";

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
                           "	   Foreign Key (USER_TYPE_ID) references USER_TYPES(ID)," +
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
                           "	   SITE_URL			VARCHAR(500) NOT NULL," +
                           "	   FACEBOOK_PAGE	VARCHAR(500) NOT NULL," +
                           "	   PHONE			VARCHAR(15) NOT NULL," +
                           "	   USER_ID			INTEGER UNSIGNED NOT NULL," +
                           "	   EXTRA_DETAILS	VARCHAR(200) NOT NULL," +
                           "	   LOGO_URL			VARCHAR(500) NOT NULL," +
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
                           "	   LOGO_URL	 	VARCHAR(500) NOT NULL," +
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
                           "	   PICTURE_URL 	VARCHAR(500) NOT NULL," +
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
        	
        	initDecodes(connection);
        	initSampleData(connection);
        	
        }
        catch (SQLException e)
        {
            System.err.println("error initializing the DB");
        }
    }

	private static void initSampleData(Connection connection) throws SQLException
	{
		Statement stmt = connection.createStatement();
		ResultSet rs;
		
        System.out.println("-----------------");

        System.out.println("Adding users");
        stmt.executeUpdate("INSERT INTO USERS (USER_NAME, PASSWORD, USER_TYPE_ID) VALUES('LinkPark', 'Link123', 2)", 
        					Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int linkUserID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO USERS (USER_NAME, PASSWORD, USER_TYPE_ID) VALUES('Aqua', 'Aqua123', 2)", 
        					Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int aquaUserID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO USERS (USER_NAME, PASSWORD, USER_TYPE_ID) VALUES('tlvStd', 'p@ssw0rd', 1)",
        					Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int tlvUserID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO USERS (USER_NAME, PASSWORD, USER_TYPE_ID) VALUES('mscWrd', 'aaabbb1', 1)", 
        					Statement.RETURN_GENERATED_KEYS);

        rs = stmt.getGeneratedKeys();
        rs.next();
        int mscWorldUserID = rs.getInt(1);       
        
        System.out.println("-----------------");

        System.out.println("Adding Bands");

        stmt.executeUpdate("INSERT INTO BANDS (BAND_NAME, GENRE, USER_ID, LOGO_URL) " +
        				   "VALUES('Linkin Park', 'Rock', " + linkUserID + 
        				   ", 'http://upload.wikimedia.org/wikipedia/commons/thumb/4/41/LPLogo-black_2000-07.svg/248px-LPLogo-black_2000-07.svg.png')", 
        				   Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int linkBandID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO BANDS (BAND_NAME, GENRE, USER_ID, LOGO_URL) " +
				   		   "VALUES('Aqua', 'Pop', " + aquaUserID + ", 'http://vkontakte.dj/cat/image/1243/AQUA.jpg')", 
				   		   Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int aquaBandID = rs.getInt(1);
        
        System.out.println("-----------------");

        System.out.println("Adding Band Members");

        stmt.executeUpdate("INSERT INTO BAND_MEMBERS (MEMBER_NAME, ROLE, BAND_ID, PICTURE_URL) " +
        				   "VALUES('Chester Bennington', 'Singer', " + linkBandID + 
        				   ", 'http://images2.fanpop.com/image/photos/11700000/CB-chester-bennington-11791491-532-643.jpg')", 
        				   Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int chesterID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO BAND_MEMBERS (MEMBER_NAME, ROLE, BAND_ID, PICTURE_URL) " +
				   "VALUES('Mike Shinoda', 'Singer', " + linkBandID + 
				   ", 'http://cdn.pigeonsandplanes.com/wp-content/uploads/2013/09/shinoda3243.jpg')", 
				   Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int shinodaID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO BAND_MEMBERS (MEMBER_NAME, ROLE, BAND_ID, PICTURE_URL) " +
				   "VALUES('Joe Hahn', 'DJ', " + linkBandID + 
				   ", 'http://images2.fanpop.com/image/photos/11200000/Joe-Hahn-joe-hahn-11285555-266-316.jpg')", 
				   Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int hahnID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO BAND_MEMBERS (MEMBER_NAME, ROLE, BAND_ID, PICTURE_URL) " +
				   "VALUES('Lene Nystrom Rasted', 'Singer', " + aquaBandID + 
				   ", 'http://www.entertainmentwallpaper.com/images/desktops/celebrity/lene_nystrom01.jpg')", 
				   Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int leneID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO BAND_MEMBERS (MEMBER_NAME, ROLE, BAND_ID, PICTURE_URL) " +
				   "VALUES('Claus Norreen', 'Keyboards', " + aquaBandID + 
				   ", 'http://showbizgeek.com/wp-content/uploads/2012/11/claus-norreen.jpg')", 
				   Statement.RETURN_GENERATED_KEYS);
                
        rs = stmt.getGeneratedKeys();
        rs.next();
        int clausID = rs.getInt(1);
                
        System.out.println("-----------------");

        System.out.println("Adding Band Members Instruments");

        stmt.executeUpdate("INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) " +
        				   "VALUES(" + chesterID + ", 20)");
        
        stmt.executeUpdate("INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) " +
				   "VALUES(" + shinodaID + ", 20)");
        
        stmt.executeUpdate("INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) " +
				   "VALUES(" + shinodaID + ", 4)");
        
        stmt.executeUpdate("INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) " +
				   "VALUES(" + hahnID + ", 12)");
        
        stmt.executeUpdate("INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) " +
				   "VALUES(" + hahnID + ", 11)");
        
        stmt.executeUpdate("INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) " +
				   "VALUES(" + leneID + ", 20)");
        
        stmt.executeUpdate("INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) " +
				   "VALUES(" + clausID + ", 12)");
        
        stmt.executeUpdate("INSERT INTO MEMBER_INSTRUMENT (MEMBER_ID, EQUIPMENT_TYPE_ID) " +
				   "VALUES(" + clausID + ", 10)");
        
        stmt = connection.createStatement();
        
        System.out.println("-----------------");

        System.out.println("Adding Studios");

        stmt.executeUpdate("INSERT INTO STUDIOS (STUDIO_NAME, CITY_ID, ADDRESS, EMAIL, CONTACT_NAME, PHONE, USER_ID, EXTRA_DETAILS, " +
        				   " FACEBOOK_PAGE, SITE_URL, LOGO_URL) " +
        				   "VALUES('TLV studios', 1, 'Ben Yehoda 39', 'tlv_studios@gmail.com', 'Avi Cohen', '050-55660243', " +
        				   tlvUserID + ", 'Best studio in Tel Aviv!', 'http://www.facebook.com/tlv.studios', "+
        				   "'http://www.tlvstudios.co.il', 'http://www.allenby.co.il/sites/default/files/styles/campteaser/public/20_0.jpg')",
        				   Statement.RETURN_GENERATED_KEYS);
     
        rs = stmt.getGeneratedKeys();
        rs.next();
        int tlvStudioID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO STUDIOS (STUDIO_NAME, CITY_ID, ADDRESS, EMAIL, CONTACT_NAME, PHONE, USER_ID, EXTRA_DETAILS, " +
				   		   " FACEBOOK_PAGE, SITE_URL, LOGO_URL) " +
						   "VALUES('Music World', 2, 'Ben Gurion 54', 'musicworld@gmail.com', 'Moshe Moshe', '052-56140263', " +
						   mscWorldUserID + ", 'We know music!', 'http://www.facebook.com/msc.world', "+
						   "'http://www.musicworld.co.il', 'https://lh5.googleusercontent.com/-yHmlcK-QYKU/UZ93xYp8CnI/AAAAAAAAAC8/_um6-5aNmAo/s0/Music_world_logo_round.jpg')",
						   Statement.RETURN_GENERATED_KEYS);

        rs = stmt.getGeneratedKeys();
        rs.next();
        int mscWorldStudioID = rs.getInt(1);

        System.out.println("-----------------");

        System.out.println("Adding Rooms");
        
        stmt.executeUpdate("INSERT INTO ROOMS (STUDIO_ID, RATE, ROOM_NAME, EXTRA_DETAILS) " +
						   "VALUES(" + tlvStudioID + ", 90, 'Big recording', 'The big room in the studio')",
						   Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int tlvBigRoomID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO ROOMS (STUDIO_ID, RATE, ROOM_NAME, EXTRA_DETAILS) " +
						   "VALUES(" + tlvStudioID + ", 70, 'Small recording', 'Only for vocal recording')",
						   Statement.RETURN_GENERATED_KEYS);
        
        rs = stmt.getGeneratedKeys();
        rs.next();
        int tlvSmallRoomID = rs.getInt(1);
        
        stmt.executeUpdate("INSERT INTO ROOMS (STUDIO_ID, RATE, ROOM_NAME, EXTRA_DETAILS) " +
						   "VALUES(" + mscWorldStudioID + ", 75, 'Production', 'DA BOMB!')",
						   Statement.RETURN_GENERATED_KEYS);

        rs = stmt.getGeneratedKeys();
        rs.next();
        int mscWorldRoomID = rs.getInt(1);
        
        System.out.println("-----------------");

        System.out.println("Adding Room-Room Types");
        
        stmt.executeUpdate("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) " +
						   "VALUES(" + tlvBigRoomID + ", 1)");
        
        stmt.executeUpdate("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) " +
						   "VALUES(" + tlvBigRoomID + ", 2)");
        
        stmt.executeUpdate("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) " +
						   "VALUES(" + tlvBigRoomID + ", 3)");
        
        stmt.executeUpdate("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) " +
						   "VALUES(" + tlvSmallRoomID + ", 2)");
        
        stmt.executeUpdate("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) " +
						   "VALUES(" + mscWorldRoomID + ", 1)");
     
	     stmt.executeUpdate("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) " +
						   "VALUES(" + mscWorldRoomID + ", 2)");
	     
	     stmt.executeUpdate("INSERT INTO ROOM_ROOM_TYPES (ROOM_ID, TYPE_ID) " +
						   "VALUES(" + mscWorldRoomID + ", 3)");
	     
	     stmt = connection.createStatement();
	        
	     System.out.println("-----------------");

	     System.out.println("Adding Rooms Equipment");
	     
	     stmt.executeUpdate("INSERT INTO ROOM_EQUIPMENT (ROOM_ID, EQUIPMENT_TYPE_ID, MODEL, MANUFACTURER, QUANTITY) " +
				   			"VALUES(" + tlvBigRoomID + ", 4, 'Fender Telecaster', 'Fender', 2)");

	     stmt.executeUpdate("INSERT INTO ROOM_EQUIPMENT (ROOM_ID, EQUIPMENT_TYPE_ID, MODEL, MANUFACTURER, QUANTITY) " +
		   					"VALUES(" + tlvBigRoomID + ", 7, 'Sonor Force 2007', 'Sonor', 1)");
	     
	     stmt.executeUpdate("INSERT INTO ROOM_EQUIPMENT (ROOM_ID, EQUIPMENT_TYPE_ID, MODEL, MANUFACTURER, QUANTITY) " +
		   					"VALUES(" + mscWorldRoomID + ", 4, 'Fender Telecaster', 'Fender', 2)");
	     
	     stmt.executeUpdate("INSERT INTO ROOM_EQUIPMENT (ROOM_ID, EQUIPMENT_TYPE_ID, MODEL, MANUFACTURER, QUANTITY) " +
							"VALUES(" + mscWorldRoomID + ", 7, 'Sonor Force 2007', 'Sonor', 1)");
	     
	     stmt.executeUpdate("INSERT INTO ROOM_EQUIPMENT (ROOM_ID, EQUIPMENT_TYPE_ID, MODEL, MANUFACTURER, QUANTITY) " +
							"VALUES(" + mscWorldRoomID + ", 12, 'Yamaha 2000', 'Yamaha', 1)");
	     
	     System.out.println("-----------------");

	     System.out.println("Adding Reviews");
	     
	     stmt.executeUpdate("INSERT INTO REVIEWS (STUDIO_ID, RATING, COMMENT, USER_ID) " +
							"VALUES(" + tlvStudioID + ", 4, 'Pretty good...', " + linkUserID + ")");
	     
	     stmt.executeUpdate("INSERT INTO REVIEWS (STUDIO_ID, RATING, COMMENT, USER_ID) " +
							"VALUES(" + mscWorldStudioID + ", 2, 'Bad manager', " + linkUserID + ")");
	     
	     stmt.executeUpdate("INSERT INTO REVIEWS (STUDIO_ID, RATING, COMMENT, USER_ID) " +
							"VALUES(" + tlvStudioID + ", 5, 'Very nice!', " + aquaUserID + ")");
	     
	     System.out.println("-----------------");

	     System.out.println("Adding Schedule");
	     
	     java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     
	     java.util.Date start = new java.util.Date(115, 0, 5, 16, 0);
	     java.util.Date end = new java.util.Date(115, 0, 5, 17, 0);

	     String startString = sdf.format(start);
	     String endString = sdf.format(end);
	     
	     stmt.executeUpdate("INSERT INTO ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) " +
							"VALUES(" + tlvBigRoomID + ", " + aquaBandID + ", '" + startString + "', '" + endString + "')");
	     
	     start = new java.util.Date(115, 0, 7, 17, 30);
	     end = new java.util.Date(115, 0, 7, 19, 0);

	     startString = sdf.format(start);
	     endString = sdf.format(end);
	     
	     stmt.executeUpdate("INSERT INTO ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) " +
							"VALUES(" + tlvBigRoomID + ", " + aquaBandID + ", '" + startString + "', '" + endString + "')");
	     
	     start = new java.util.Date(115, 1, 22, 15, 45);
	     end = new java.util.Date(115, 1, 22, 16, 30);

	     startString = sdf.format(start);
	     endString = sdf.format(end);
	     
	     stmt.executeUpdate("INSERT INTO ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) " +
							"VALUES(" + tlvSmallRoomID + ", " + linkBandID + ", '" + startString + "', '" + endString + "')");

	     start = new java.util.Date(115, 0, 8, 18, 45);
	     end = new java.util.Date(115, 0, 8, 20, 15);
		
	     startString = sdf.format(start);
	     endString = sdf.format(end);
	     
	     stmt.executeUpdate("INSERT INTO ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) " +
							"VALUES(" + mscWorldRoomID + ", " + linkBandID + ", '" + startString + "', '" + endString + "')");
	}
	
	private static void initDecodes(Connection connection) throws SQLException
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

        System.out.println("-----------------");

        System.out.println("Adding Cities");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(1, 'Tel Aviv', 1)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(2, 'Ramat Gan', 1)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(3, 'Haifa', 2)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(4, 'Tveria', 2)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(5, 'Eilat', 3)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(6, 'Beer Sheva', 3)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(7, 'Jerusalem', 4)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(8, 'Beit Shemesh', 4)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(9, 'Natania', 5)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(10, 'Raanana', 5)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(11, 'Rehovot', 6)");
        stmt.executeUpdate("INSERT INTO CITIES VALUES(12, 'Ashdod', 6)");
       
        System.out.println("-----------------");

        System.out.println("Adding User Types");
        stmt.executeUpdate("INSERT INTO USER_TYPES VALUES(1, 'Studio')");
        stmt.executeUpdate("INSERT INTO USER_TYPES VALUES(2, 'Band')");
        
        System.out.println("-----------------");

        System.out.println("Adding Room Types");
        stmt.executeUpdate("INSERT INTO ROOM_TYPES VALUES(1, 'Rehearsal')");
        stmt.executeUpdate("INSERT INTO ROOM_TYPES VALUES(2, 'Recording')");
        stmt.executeUpdate("INSERT INTO ROOM_TYPES VALUES(3, 'Music Production')");
        
        
        System.out.println("-----------------");

        System.out.println("Adding Equipment categories");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_CATEGORIES VALUES(1, 'Microphone')");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_CATEGORIES VALUES(2, 'Guitars')");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_CATEGORIES VALUES(3, 'Bass')");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_CATEGORIES VALUES(4, 'Drums')");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_CATEGORIES VALUES(5, 'Keyboards')");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_CATEGORIES VALUES(6, 'Amplifier')");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_CATEGORIES VALUES(7, 'Monitor')");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_CATEGORIES VALUES(8, 'Other Equipment')");
        
        System.out.println("-----------------");

        System.out.println("Adding Equipment types");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(1, 'Simple Microphone', 1)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(2, 'Condenser', 1)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(3, 'Acoustic Guitar', 2)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(4, 'Electric Guitar', 2)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(5, 'Double Bass', 3)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(6, 'Electric Bass Guitar', 3)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(7, 'Drum Set', 4)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(8, 'Snare Drum', 4)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(9, 'Cymbals', 4)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(10, 'Piano', 5)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(11, 'Synthesizer', 5)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(12, 'Electric Keys', 5)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(13, 'Electric Guitar Amp', 6)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(14, 'Bass Guitar Amp', 6)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(15, 'Singing Monitor', 7)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(16, 'Room Monitor', 7)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(17, 'Headphones', 8)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(18, 'Percussion', 8)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(19, 'Bar Chair', 8)");
        stmt.executeUpdate("INSERT INTO EQUIPMENT_TYPES VALUES(20, 'Vocals', 8)");
	}
	
    public static String getAreas()
    {
        JSONObject result = new JSONObject();
        
        String selectionString = "select * from AREAS";
        
        try
        {
            // Create a result set containing all data from my_table
            Statement stmt = GetConnection().createStatement();
            stmt.executeUpdate("USE musicRoomDB");
            
            // Executed query
            ResultSet rs = stmt.executeQuery(selectionString);

            // Fetch each row from the result set
            while (rs.next())
            {
            	JSONObject current = new JSONObject();
            	current.put("ID", rs.getInt("ID"));
            	current.put("AREA_NAME", rs.getString("AREA_NAME"));
            	
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
