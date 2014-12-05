package com.musicroom.servlet.requests_api;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.database.MainDBHandler;
import com.musicroom.utils.JSONUtils;

@Path("/bands")
public class BandsRequestsAPI {
	
	private static final String BAND_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Band with id '%d' was not found\"}";
	
	@GET
	@Produces("application/json")
	public Response getBands() {
		JSONArray results = MainDBHandler.getStatementResult("select * from BANDS");
		
		return Response.ok(results.toString()).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response getBandByID(@PathParam("id") int id) {
    	JSONArray results = 
    			MainDBHandler.getPreparedStatementResult("select * from BANDS where id = ?", id);
		
		JSONObject result = JSONUtils.extractJSONObject(results);
		
		if(result.length() > 0) {
			return Response.ok(result.toString()).build();
		}
		else {
			String errorJson = String.format(BAND_ID_WAS_NOT_FOUND_ERROR_JSON, id);
			return Response.status(HttpServletResponse.SC_NOT_FOUND).entity(errorJson).build();
		}	
	}	
}
