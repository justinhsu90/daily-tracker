package com.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ListView;

public class Tab_PlaceFetch extends Activity {
	private JSONObject location = new JSONObject();
	protected ListView placesList;
	protected LocationManager lm;
	protected static JSONArray jsonArray;
	SQLiteDatabase db;
	public int ID;

	Double LON;
	Double LAT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		ID = extras.getInt("id");
		LON = Double.valueOf(extras.getString("longitude"));
		LAT = Double.valueOf(extras.getString("latitude"));
		GetPlace(LAT, LON);
	}

	public void GetPlace(Double Lat, Double Lon) {
		try {
			location.put("latitude", new Double(Lat));
			location.put("longitude", new Double(Lon));
			Bundle params = new Bundle();
			params.putString("type", "place");
			params.putString("center", location.getString("latitude") + "," + location.getString("longitude"));
			params.putString("distance", "50");
			FB_Utility.mAsyncRunner.request("search", params, new placesRequestListener());
		} catch (Exception e) {
		}
	}

	/*
	 * Callback after places are fetched.
	 */
	public class placesRequestListener extends FB_BaseRequestListener {
		@Override
		public void onComplete(final String response, final Object state) {
			try {
				jsonArray = new JSONObject(response).getJSONArray("data");
				if (jsonArray == null) {
					return;
				}
			} catch (JSONException e) {
				return;
			}
			JSONObject jsonObject = null;
			try {
				jsonObject = jsonArray.getJSONObject(0);
				update(ID, jsonObject.getString("name"), jsonObject.getString("category"));
			} catch (JSONException e) {
				update(ID, "Not Found", "Not Found");
			}
		}

		public void onFacebookError(FB_FacebookError error) {
		}
	}

	public void update(int id, String place, String place_type) {
		final Intent Intent = new Intent();
		Intent.setClass(Tab_PlaceFetch.this, TabSQLite_StorePlaceToDB.class);
		Intent.putExtra("id", id);
		Intent.putExtra("place", place);
		Intent.putExtra("place_type", place_type);
		startActivity(Intent);
		Tab_PlaceFetch.this.finish();

	}
}
