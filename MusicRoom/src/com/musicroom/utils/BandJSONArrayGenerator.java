package com.musicroom.utils;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

public class BandJSONArrayGenerator {
	HashMap<Integer, JSONObject> bandIDToObject;
	HashMap<Integer, JSONObject> bandMemberIDToObject;

	public BandJSONArrayGenerator() {
		bandIDToObject = new HashMap<Integer, JSONObject>();
		bandMemberIDToObject = new HashMap<Integer, JSONObject>();
	}

	public JSONArray createBandsArray(JSONArray selectResult) {
		reset();
		JSONArray bandsArray = new JSONArray();

		for (int i = 0; i < selectResult.length(); i++) {
			JSONObject currentRow = selectResult.getJSONObject(i);

			JSONObject currentBand = getOrCreateBand(currentRow, bandsArray);
			JSONObject currentBandMember = getOrCreateBandMember(currentRow,
					currentBand);
			addInstrument(currentRow, currentBandMember);
	
		}

		return bandsArray;
	}

	private JSONObject getOrCreateBand(JSONObject currentRow,
			JSONArray bandsArray) {
		int bandID = currentRow.getInt("BAND_ID");
		JSONObject bandObject = bandIDToObject.getOrDefault(bandID, null);

		if (bandObject == null) {
			bandObject = createBandObject(currentRow);
			bandIDToObject.put(bandID, bandObject);
			bandsArray.put(bandObject);
		}

		return bandObject;
	}

	private JSONObject createBandObject(JSONObject currentRow) {
		Integer bandID = currentRow.getInt("BAND_ID");
		Integer userID = currentRow.getInt("USER_ID");
		String name = currentRow.getString("BAND_NAME");
		String logo = currentRow.getString("LOGO_URL");
		String genre = currentRow.getString("GENRE");

		return new JSONObject(new JSONStringer()
								.object()
									.key("_id").value(bandID)
									.key("user_id").value(userID)
									.key("name").value(name)
									.key("logo").value(logo)
									.key("genre").value(genre)
									.key("band_members")
										.array()
										.endArray()
								.endObject()
								.toString());

	}

	private JSONObject getOrCreateBandMember(JSONObject currentRow,
			JSONObject bandObject) {
		Integer bandMemberID = currentRow.getInt("MEMBER_ID");
		JSONObject bandMemberObject = bandMemberIDToObject.getOrDefault(
				bandMemberID, null);

		if (bandMemberObject == null) {
			bandMemberObject = createBandMemberObject(currentRow);
			bandMemberIDToObject.put(bandMemberID, bandMemberObject);
			bandObject.getJSONArray("band_members").put(bandMemberObject);
		}

		return bandMemberObject;
	}

	private JSONObject createBandMemberObject(JSONObject currentRow) {
		Integer bandMemberID = currentRow.getInt("MEMBER_ID");
		String name = currentRow.getString("MEMBER_NAME");
		String role = currentRow.getString("ROLE");
		String picture = currentRow.getString("PICTURE_URL");

		return new JSONObject(new JSONStringer()
									.object()
										.key("_id").value(bandMemberID)
										.key("name").value(name)
										.key("role").value(role)
										.key("picture").value(picture)
										.key("instruments")
											.array()
											.endArray()
									.endObject()
									.toString());
	}
	
	private void addInstrument(JSONObject currentRow,
			JSONObject currentBandMember) {
		String instrument = currentRow.getString("EQUIPMENT_NAME");		
		currentBandMember.getJSONArray("instruments").put(instrument);
	}

	private void reset() {
		bandIDToObject.clear();
		bandMemberIDToObject.clear();
	}
}
