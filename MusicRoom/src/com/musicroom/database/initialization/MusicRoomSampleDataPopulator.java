package com.musicroom.database.initialization;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.musicroom.database.interfaces.MySQLDataBasePopulator;

public class MusicRoomSampleDataPopulator implements MySQLDataBasePopulator {

	@Override
	public void initDBData(Connection connection) throws SQLException
    {
        try
        {
        	
        	initDecodes(connection);
        	initSampleData(connection);
        	
        }
        catch (SQLException e)
        {
            System.err.println("error initializing the DB:\n" + e.toString());
        }
    }

	private void initSampleData(Connection connection) throws SQLException
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

	     java.util.Date start = getTodayTime(17, 0);
	     java.util.Date end = getTodayTime(17, 15);
	     String startString = sdf.format(start);
	     String endString = sdf.format(end);

	     stmt.executeUpdate("INSERT INTO ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) " +
							"VALUES(" + tlvBigRoomID + ", " + aquaBandID + ", '" + startString + "', '" + endString + "')");
	     
	     start =  getTodayTime(17, 30);
	     end = getTodayTime(19, 0);

	     startString = sdf.format(start);
	     endString = sdf.format(end);
	     
	     stmt.executeUpdate("INSERT INTO ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) " +
							"VALUES(" + tlvBigRoomID + ", " + aquaBandID + ", '" + startString + "', '" + endString + "')");
	     
	     start = getTodayTime(15, 45);
	     end = getTodayTime(16, 30);

	     startString = sdf.format(start);
	     endString = sdf.format(end);
	     
	     stmt.executeUpdate("INSERT INTO ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) " +
							"VALUES(" + tlvSmallRoomID + ", " + linkBandID + ", '" + startString + "', '" + endString + "')");

	     start = getTodayTime(18, 45);
	     end = getTodayTime(20, 15);
		
	     startString = sdf.format(start);
	     endString = sdf.format(end);
	     
	     stmt.executeUpdate("INSERT INTO ROOM_SCHEDULE (ROOM_ID, BAND_ID, START_TIME, END_TIME) " +
							"VALUES(" + mscWorldRoomID + ", " + linkBandID + ", '" + startString + "', '" + endString + "')");
	}
	
	private void initDecodes(Connection connection) throws SQLException
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
	
	private java.util.Date getTodayTime(int hours, int minutes) {
        Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), 
					 calendar.get(Calendar.MONTH), 
					 calendar.get(Calendar.DAY_OF_MONTH), 
					 hours, 
					 minutes, 
					 0);
	    
		return calendar.getTime();
	}

}
