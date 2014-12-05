package com.musicroom.servlet.requests_api;

import javax.servlet.http.HttpServletRequest;

public class SessionManager {

    static final String USERNAME = "Username";

    public static String getLoggedInUsername(HttpServletRequest request)
    {
        Object sessionAttribute = request.getSession().getAttribute(USERNAME);
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void setLoggedInUsername(HttpServletRequest request, String name)
    {
        request.getSession().setAttribute(USERNAME, name);
    }
}
