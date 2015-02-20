package com.musicroom.resources;

import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.database.MainDBHandler;
import com.musicroom.resources.filters.RequireLoggedBand;
import com.musicroom.session.SessionManager;

@Path("/reviews")
public class ReviewsResource {

	private static final int SPACE_TO_INDENTS_EACH_LEVEL = 2;
	private static final String REVIEW_EXISTS = "{\"error\":\"You can only add one review per studio.\"}";
	private static final String BAD_RATE = "{\"error\":\"The rating of the review must be between 1 and 5.\"}";

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudioReviews(@PathParam("id") int id) throws Exception {
		JSONArray reviews;
		
		reviews = MainDBHandler
				.selectWithParameters(
						"select * "
								+ "from REVIEWS as r left join BANDS as b on r.USER_ID = b.USER_ID "
								+ "where r.STUDIO_ID = ?", id);

		JSONArray results = new JSONArray();

		for (int i = 0; i < reviews.length(); i++) {
			JSONObject curr = reviews.getJSONObject(i);

			JSONObject review = new JSONObject();
			review.put("studio_id", curr.getInt("studio_id"));
			review.put("comment", curr.getString("comment"));
			review.put("rating", curr.getInt("rating"));
			review.put("user_id", curr.getInt("user_id"));
			review.put("user_name", curr.getString("band_name"));
			review.put("user_logo", curr.getString("logo_url"));
			
			results.put(review);
		}

		return Response.ok(results.toString(SPACE_TO_INDENTS_EACH_LEVEL)).build();
	}

	@POST
	@RequireLoggedBand
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addReview(String dataStr,
			@Context HttpServletRequest Request) throws Exception {
		JSONObject data = new JSONObject(dataStr);
		JSONObject loggedUser = SessionManager.getLoggedInUser(Request);

		int userId = loggedUser.getInt("id");
		int studioId = data.getInt("studio_id");

		// Get user review count for this studio
		JSONArray existingReview = MainDBHandler
				.selectWithParameters(
						"select count(*) as COUNT from REVIEWS where USER_ID = ? and STUDIO_ID = ?",
						userId, studioId);

		// Check if there is an existing review
		if (existingReview.getJSONObject(0).getInt("count") > 0) {
			return Response
					.status(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
					.entity(REVIEW_EXISTS).build();
		} else {
			// Validate rating
			int rating = data.getInt("rating");
			if (rating < 0 || rating > 5) {
				return Response
						.status(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
						.entity(BAD_RATE).build();
			} else {
				MainDBHandler
						.insertWithAutoKey(
								"INSERT INTO REVIEWS (USER_ID, STUDIO_ID, RATING, COMMENT) VALUES(?, ?, ?, ?)", PreparedStatement.NO_GENERATED_KEYS,
								userId, studioId, rating,
								data.getString("comment"));

				return Response.ok("{\"message\": \"success\"}").build();
			}
		}
	}
}
