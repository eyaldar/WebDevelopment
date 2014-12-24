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
		int bandID = currentRow.getInt("band_id");
		JSONObject bandObject = bandIDToObject.getOrDefault(bandID, null);

		if (bandObject == null) {
			bandObject = createBandObject(currentRow);
			bandIDToObject.put(bandID, bandObject);
			bandsArray.put(bandObject);
		}

		return bandObject;
	}

	private JSONObject createBandObject(JSONObject currentRow) {
		Integer bandID = currentRow.getInt("band_id");
		Integer userID = currentRow.getInt("user_id");
		String name = currentRow.getString("band_name");
		String logo = currentRow.getString("logo_url");
		String genre = currentRow.getString("genre");

		return new JSONObject(new JSONStringer()
								.object()
									.key("id").value(bandID)
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
		Integer bandMemberID = currentRow.getInt("member_id");
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
		Integer bandMemberID = currentRow.getInt("member_id");
		String name = currentRow.getString("member_name");
		String role = currentRow.getString("role");
		String picture = currentRow.getString("picture_url");

		return new JSONObject(new JSONStringer()
									.object()
										.key("id").value(bandMemberID)
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
		int instrument = currentRow.getInt("equipment_type_id");		
		currentBandMember.getJSONArray("instruments").put(instrument);
	}

	private void reset() {
		bandIDToObject.clear();
		bandMemberIDToObject.clear();
	}
}
