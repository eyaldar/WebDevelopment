package com.musicroom.rest.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.musicroom.database.MainDBHandler;
import com.musicroom.session.SessionManager;

@Path("/reviews")
public class ReviewsRequestsAPI
{
	
	private static final String NOT_LOGGED = "{\"error\":\"You must log in to add a review.\"}";
	private static final String REVIEW_EXISTS = "{\"error\":\"You can only add one review per studio.\"}";
	private static final String BAD_RATE = "{\"error\":\"The rating of the review must be between 1 and 5.\"}";
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response getStudioReviews(@PathParam("id") int id)
	{
		JSONArray reviews = 
				MainDBHandler.getPreparedStatementResult("select * "
													   + "from REVIEWS as r left join USERS as u on r.USER_ID = u.ID "
													   + "where r.STUDIO_ID = ?", id);

		JSONArray results = new JSONArray();
		
		for (int i = 0; i < reviews.length(); i++)
		{
			JSONObject curr = reviews.getJSONObject(i);

			JSONObject review = new JSONObject();
			review.put("STUDIO_ID", curr.getInt("STUDIO_ID"));
			review.put("COMMENT", curr.getString("COMMENT"));
			review.put("RATING", curr.getInt("RATING"));
			review.put("USER_ID", curr.getInt("USER_ID"));
			review.put("USER_NAME", curr.getString("USER_NAME"));
			review.put("USER_TYPE_ID", curr.getInt("USER_TYPE_ID"));

			results.put(review);
		} 
		
		return Response.ok(results.toString()).build();
	}

	@POST
	@Produces("application/json")
	@Consumes
	public Response AddReview(String dataStr, @Context HttpServletRequest Request)
	{
		JSONObject data = new JSONObject("dataStr");
		JSONObject loggedUser = SessionManager.getLoggedInUser(Request);

		// Check if there is no logged user
		if (loggedUser == null)
		{
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(NOT_LOGGED).build();
		}
		else
		{
			int userId = loggedUser.getInt("ID");
			int studioId = data.getInt("STUDIO_ID");
			
			// Get user review count for this studio
			JSONArray existingReview = 
					MainDBHandler.getPreparedStatementResult("select count(*) as COUNT from REVIEWS where USER_ID = ? and STUDIO_ID = ?", 
							userId, studioId);
			
			// Check if there is an existing review
			if (existingReview.getJSONObject(0).getInt("COUNT") > 0)
			{
				return Response.status(HttpServletResponse.SC_METHOD_NOT_ALLOWED).entity(REVIEW_EXISTS).build();
			}
			else
			{
				// Validate rating
				int rating = data.getInt("RATING");
				if (rating < 0 || rating > 5)
				{
					return Response.status(HttpServletResponse.SC_METHOD_NOT_ALLOWED).entity(BAD_RATE).build();
				}
				else
				{
					MainDBHandler.getPreparedStatementResult("INSERT INTO REVIEWS (USER_ID, STUDIO_ID, RATING, COMMENT) VALUES(?, ?, ?, ?)",
							userId, studioId, rating, data.getString("COMMENT"));
					
					return Response.ok("{message: \"success\"}").build();
				}
			}
		}
	}
}
