/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musicroom.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.musicroom.database.MainDBHandler;
import com.musicroom.utils.RequestQueryParser;

@WebServlet("/ResetMySQLDB")
public class InitDataBaseServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4356429129777704196L;
	private static final String RESET_MYSQL_DB_USERNAME = "root";
	private static final String RESET_MYSQL_DB_PASSWORD = "root";
    private static final String INVALID_USER_OR_PASSWORD_RESPONSE_STRING = "Invalid user or password!";
    private static final String SUCCESS_RESPONSE_STRING = "Success!";
    private static final String FAILED_RESPONSE_STRING = "Reset failed!";

	/** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
        	String query = request.getQueryString();
        	if(query != null) {
        		Map<String, String> queryMap = RequestQueryParser.getQueryMap(query);
        		String username = queryMap.getOrDefault("user", null);
        		String password = queryMap.getOrDefault("password", null);
            	if(RESET_MYSQL_DB_USERNAME.equals(username) &&
            	   RESET_MYSQL_DB_PASSWORD.equals(password)){
                    createAndFillDB();
                    out.write(SUCCESS_RESPONSE_STRING);
            	}
            	else{
            		out.write(INVALID_USER_OR_PASSWORD_RESPONSE_STRING);
            	}	
        	}
        	else {
        		out.write(INVALID_USER_OR_PASSWORD_RESPONSE_STRING);
        	}
        }
        catch (Exception e){
        	out.write(FAILED_RESPONSE_STRING);
        }
        finally {
            out.close();
        }
    }

	private void createAndFillDB() {
		Connection con = MainDBHandler.GetConnection();
        try {

        	MainDBHandler.createDB(con);

        	MainDBHandler.InitDBData(con);
            System.out.println("Create database scheme");
        }
        catch (SQLException ex) {
            Logger.getLogger(InitDataBaseServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>
}
