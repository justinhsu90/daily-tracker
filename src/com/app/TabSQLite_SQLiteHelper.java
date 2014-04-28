package com.app;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TabSQLite_SQLiteHelper extends SQLiteOpenHelper {

	private static final String DB_FILE = "History.db";
	private static final int DB_VERSION = 1;
	public static final String TABLE_NAME = "Track_info";
	ArrayList<HashMap<String, Object>> listData;
	NumberFormat NF = NumberFormat.getInstance();

	public TabSQLite_SQLiteHelper(Context context) {
		super(context, TabSQLite_SQLiteHelper.DB_FILE, null, TabSQLite_SQLiteHelper.DB_VERSION);
	}

	public TabSQLite_SQLiteHelper(Context context, Activity parent) {
		super(context, TabSQLite_SQLiteHelper.DB_FILE, null, TabSQLite_SQLiteHelper.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS " + TabSQLite_SQLiteHelper.TABLE_NAME + " (");

		sql.append(" ID INTEGER PRIMARY KEY autoincrement");
		sql.append(", TIME INTEGER");
		sql.append(", LATITUDE TEXT");
		sql.append(", LONGITUDE TEXT");
		sql.append(", PLACE TEXT");
		sql.append(", PLACE_TYPE TEXT");
		sql.append(", PLACE_GET INTEGER");
		sql.append(");");
		db.execSQL(sql.toString());
	}

	public void CreateTable(String TableName) {
		SQLiteDatabase db = getWritableDatabase();
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS " + TabSQLite_SQLiteHelper.TABLE_NAME + " (");

		sql.append(" ID INTEGER PRIMARY KEY autoincrement");
		sql.append(", TIME INTEGER");
		sql.append(", LATITUDE TEXT");
		sql.append(", LONGITUDE TEXT");
		sql.append(", PLACE TEXT");
		sql.append(", PLACE_TYPE TEXT");
		sql.append(", PLACE_GET INTEGER");
		sql.append(");");
		db.execSQL(sql.toString());

	}

	public void DeleteTable(String TableName) {
		SQLiteDatabase db = getWritableDatabase();

		db.execSQL("DROP TABLE IF EXISTS " + TableName);
	}

	public void addInfo(Long TIME, String LATITUDE, String LONGITUDE, String PLACE, String PLACE_TYPE) {
		NF.setMaximumFractionDigits(5);
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		String lat = String.valueOf(NF.format(Double.valueOf(LATITUDE)));
		String lon = String.valueOf(NF.format(Double.valueOf(LONGITUDE)));

		if (TIME == 0) {
			values.put("time", System.currentTimeMillis());
		} else {
			values.put("time", TIME);
		}
		values.put("latitude", lat);
		values.put("longitude", lon);
		values.put("place", PLACE);
		values.put("place_type", PLACE_TYPE);
		values.put("place_get", 0);
		db.insertOrThrow(TabSQLite_SQLiteHelper.TABLE_NAME, null, values);
	}

	public int updateDB(int id, String column, String value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(column, value);
		return db.update(TabSQLite_SQLiteHelper.TABLE_NAME, args, "ID=" + id, null);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}