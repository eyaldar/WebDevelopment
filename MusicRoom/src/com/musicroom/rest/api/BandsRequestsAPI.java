package com.musicroom.rest.api;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.database.MainDBHandler;
import com.musicroom.utils.JSONUtils;

@Path("/bands")
public class BandsRequestsAPI {

	private static final String BAND_ID_WAS_NOT_FOUND_ERROR_JSON = "{\"error\":\"Band with id '%d' was not found\"}";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBands() {
		JSONArray results;
		try {
			results = MainDBHandler.select("select * from BANDS");
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}

		return Response.ok(results.toString()).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBandByID(@PathParam("id") int id) {
		JSONArray results;
		try {
			results = MainDBHandler.selectWithParameters(
					"select * from BANDS where id = ?", id);
			JSONObject result = JSONUtils.extractJSONObject(results);

			if (result.length() > 0) {
				return Response.ok(result.toString()).build();
			} else {
				String errorJson = String.format(
						BAND_ID_WAS_NOT_FOUND_ERROR_JSON, id);
				return Response.status(HttpServletResponse.SC_NOT_FOUND)
						.entity(errorJson).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}

	}
}
