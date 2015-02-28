package com.musicroom.resources.decodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

import com.musicroom.database.MainDBHandler;

@Path("/cities")
public class CitiesResource {

	private static final int SPACE_TO_INDENTS_EACH_LEVEL = 2;

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCitiesByArea(@PathParam("id") int id) throws Exception {
		JSONArray results = MainDBHandler
				.selectWithParameters(
						"select CITY_NAME as name, ID as id, AREA_ID as area_id "
					  + "from CITIES where AREA_ID = ?",
						id);

		return Response.ok(results.toString(SPACE_TO_INDENTS_EACH_LEVEL))
				.build();
	}

}
