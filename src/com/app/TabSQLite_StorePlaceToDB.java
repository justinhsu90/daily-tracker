package com.app;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class TabSQLite_StorePlaceToDB extends Activity {

	TabSQLite_SQLiteHelper helper;
	SQLiteDatabase db;
	int ID;
	String PLACE;
	String PLACE_TYPE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		helper = new TabSQLite_SQLiteHelper(this);
		db = helper.getReadableDatabase();
		Bundle extras = getIntent().getExtras();
		ID = extras.getInt("id");
		PLACE = extras.getString("place");
		PLACE_TYPE = extras.getString("place_type");
		updateDB(ID, PLACE, PLACE_TYPE, 1);
		TabSQLite_StorePlaceToDB.this.finish();
	}

	public void print() {
		System.out.println("#ID:   " + ID + "   Place:   " + PLACE + "   Place type:   " + PLACE_TYPE);
	}

	public int updateDB(int id, String place, String place_type, int place_get) {

		ContentValues args = new ContentValues();
		if (place.equals("")) {
			args.put("PLACE", "Not Found");
		} else {
			args.put("PLACE", place);
			if (place_type != null) {
				args.put("PLACE_TYPE", place_type);
			}
		}
		args.put("PLACE_GET", place_get);
		return db.update(TabSQLite_SQLiteHelper.TABLE_NAME, args, "ID=" + id, null);

	}

}
