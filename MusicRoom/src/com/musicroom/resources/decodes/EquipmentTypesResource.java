package com.musicroom.resources.decodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

import com.musicroom.database.MainDBHandler;

@Path("/equipment_types")
public class EquipmentTypesResource {
	
	private static final int SPACE_TO_INDENTS_EACH_LEVEL = 1;

	@GET
	@Path("/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public static Response getEquipmentTypes(
			@PathParam("category") int category) throws Exception {
		JSONArray result = MainDBHandler.selectWithParameters(
				"select EQUIPMENT_NAME as name, ID as id from EQUIPMENT_TYPES where CATEGORY_ID = ?",
				category);

		return Response.ok(result.toString(SPACE_TO_INDENTS_EACH_LEVEL)).build();
	}
}
