package com.musicroom.resources.decodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

import com.musicroom.database.MainDBHandler;

@Path("/user_types")
public class UserTypeResouce {
	private static final int SPACE_TO_INDENTS_EACH_LEVEL = 2;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public static Response getRoomTypes() throws Exception {
		JSONArray result = MainDBHandler
				.select("select DESCRIPTION as name, ID as id "
						+ "from USER_TYPES");

		return Response.ok(result.toString(SPACE_TO_INDENTS_EACH_LEVEL))
				.build();
	}
}
