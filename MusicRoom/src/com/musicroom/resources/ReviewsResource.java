package com.musicroom.resources;

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

	private static final String REVIEW_EXISTS = "{\"error\":\"You can only add one review per studio.\"}";
	private static final String BAD_RATE = "{\"error\":\"The rating of the review must be between 1 and 5.\"}";

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudioReviews(@PathParam("id") int id) {
		JSONArray reviews;
		try {
			reviews = MainDBHandler
					.selectWithParameters(
							"select * "
									+ "from REVIEWS as r left join USERS as u on r.USER_ID = u.ID "
									+ "where r.STUDIO_ID = ?", id);

			JSONArray results = new JSONArray();

			for (int i = 0; i < reviews.length(); i++) {
				JSONObject curr = reviews.getJSONObject(i);

				JSONObject review = new JSONObject();
				review.put("studio_id", curr.getInt("studio_id"));
				review.put("comment", curr.getString("comment"));
				review.put("rating", curr.getInt("rating"));
				review.put("user_id", curr.getInt("user_id"));
				review.put("user_name", curr.getString("user_name"));

				results.put(review);
			}

			return Response.ok(results.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	@POST
	@RequireLoggedBand
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response AddReview(String dataStr,
			@Context HttpServletRequest Request) {
		try {
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
							.selectWithParameters(
									"INSERT INTO REVIEWS (USER_ID, STUDIO_ID, RATING, COMMENT) VALUES(?, ?, ?, ?)",
									userId, studioId, rating,
									data.getString("comment"));

					return Response.ok("{\"message\": \"success\"}").build();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
}
