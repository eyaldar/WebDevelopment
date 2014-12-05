package com.musicroom.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONObject;

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
	
    public static String getAreas()
    {
    	JSONArray result = new JSONArray();
        
        String selectionString = "select * from AREAS";
        
        try
        {
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
            	
            	result.put(current);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in reading data" + e.toString());
        }

        return (result.toString());
    }
    
    public static String getCities()
    {
    	JSONArray result = new JSONArray();
        
        String selectionString = "select * from CITIES";
        
        try
        {
            Statement stmt = GetConnection().createStatement();
            stmt.executeUpdate("USE musicRoomDB");
            
            // Executed query
            ResultSet rs = stmt.executeQuery(selectionString);

            // Fetch each row from the result set
            while (rs.next())
            {
            	JSONObject current = new JSONObject();
            	current.put("ID", rs.getInt("ID"));
            	current.put("AREA_ID", rs.getInt("AREA_ID"));
            	current.put("CITY_NAME", rs.getString("CITY_NAME"));
            	
            	result.put(current);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in reading data");
        }

        return (result.toString());
    }
    
    public static String getUserTypes()
    {
    	JSONArray result = new JSONArray();
        
        String selectionString = "select * from USER_TYPES";
        
        try
        {
            Statement stmt = GetConnection().createStatement();
            stmt.executeUpdate("USE musicRoomDB");
            
            // Executed query
            ResultSet rs = stmt.executeQuery(selectionString);

            // Fetch each row from the result set
            while (rs.next())
            {
            	JSONObject current = new JSONObject();
            	current.put("ID", rs.getInt("ID"));
            	current.put("DESCRIPTION", rs.getString("DESCRIPTION"));
            	
            	result.put(current);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in reading data");
        }

        return (result.toString());
    }
    
    public static String getEquipmentCategories()
    {
    	JSONArray result = new JSONArray();
        
        String selectionString = "select * from EQUIPMENT_CATEGORIES";
        
        try
        {
            Statement stmt = GetConnection().createStatement();
            stmt.executeUpdate("USE musicRoomDB");
            
            // Executed query
            ResultSet rs = stmt.executeQuery(selectionString);

            // Fetch each row from the result set
            while (rs.next())
            {
            	JSONObject current = new JSONObject();
            	current.put("ID", rs.getInt("ID"));
            	current.put("EQUIP_CAT_NAME", rs.getString("EQUIP_CAT_NAME"));
            	
            	result.put(current);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in reading data");
        }

        return (result.toString());
    }
    
    public static String getEquipmentTypes()
    {
    	JSONArray result = new JSONArray();
        
        String selectionString = "select * from EQUIPMENT_TYPES";
        
        try
        {
            Statement stmt = GetConnection().createStatement();
            stmt.executeUpdate("USE musicRoomDB");
            
            // Executed query
            ResultSet rs = stmt.executeQuery(selectionString);

            // Fetch each row from the result set
            while (rs.next())
            {
            	JSONObject current = new JSONObject();
            	current.put("ID", rs.getInt("ID"));
            	current.put("CATEGORY_ID", rs.getInt("CATEGORY_ID"));
            	current.put("EQUIP_NAME", rs.getString("EQUIP_NAME"));
            	
            	result.put(current);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in reading data");
        }

        return (result.toString());
    }
    
    public static String getRoomTypes()
    {
    	JSONArray result = new JSONArray();
        
        String selectionString = "select * from ROOM_TYPES";
        
        try
        {
            Statement stmt = GetConnection().createStatement();
            stmt.executeUpdate("USE musicRoomDB");
            
            // Executed query
            ResultSet rs = stmt.executeQuery(selectionString);

            // Fetch each row from the result set
            while (rs.next())
            {
            	JSONObject current = new JSONObject();
            	current.put("ID", rs.getInt("ID"));
            	current.put("ROOM_TYPE_NAME", rs.getString("ROOM_TYPE_NAME"));
            	
            	result.put(current);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in reading data");
        }

        return (result.toString());
    }
    
    public static String getUserByLogin(JSONObject loginData)
    {
    	JSONObject result = new JSONObject();
        
        String selectionString = "select * from USERS where USER_NAME = ? and PASSWORD = ?";
        
        try
        {
        	Connection con = GetConnection();
            java.sql.PreparedStatement stmt = con.prepareStatement(selectionString);
            stmt.executeUpdate("USE musicRoomDB");
            stmt.setString(1, loginData.getString("USER_NAME"));
            stmt.setString(2, loginData.getString("PASSWORD"));
            
            // Executed query
            ResultSet rs = stmt.executeQuery();

            // Fetch each row from the result set
            if (rs.next())
            {
            	result.put("ID", rs.getInt("ID"));
            	result.put("USER_NAME", rs.getString("USER_NAME"));
            	result.put("PASSWORD", rs.getString("PASSWORD"));
            	result.put("USER_TYPE_ID", rs.getInt("USER_TYPE_ID"));
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in reading data");
        }

        return (result.toString());
    }
}
