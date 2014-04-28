package com.app;

import java.util.Timer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

public class TabSQLite_PlaceUpdateFromDB extends Activity {
	TabSQLite_SQLiteHelper helper;
	SQLiteDatabase db;
	Boolean Complete = false;
	int _id = -1;
	String _latitude = "";
	String _longitude = "";
	private long Delay = 15000;
	int count = 0;
	Timer timer;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.helper = new TabSQLite_SQLiteHelper(this);
		setContentView(R.layout.update_page);
		new asyncTaskProgress().execute();
		timer= new Timer();
	}

	final Intent myIntent = new Intent();

	/** update the place and place type by the records in the Database **/
	public Boolean getMatchInDB() {
		try {
			db = helper.getReadableDatabase();
			_id = -1;
			_latitude = "";
			_longitude = "";
			Cursor cursor = db.rawQuery("select ID,LATITUDE,LONGITUDE from " + TabSQLite_SQLiteHelper.TABLE_NAME + " where PLACE_GET=0", null);
			while (cursor.moveToNext()) {
				_id = cursor.getInt(cursor.getColumnIndex("ID"));
				_latitude = cursor.getString(cursor.getColumnIndex("LATITUDE"));
				_longitude = cursor.getString(cursor.getColumnIndex("LONGITUDE"));
				Cursor c = db.rawQuery("select PLACE,PLACE_TYPE from " + TabSQLite_SQLiteHelper.TABLE_NAME + " where LATITUDE= " + _latitude
						+ " and LONGITUDE= " + _longitude + " and PLACE <>'Not Available' and PLACE <>'Not Found' limit 1", null);
				if (c.getCount() == 0) {

//					TimerTask task = new TimerTask() {
//						@Override
//						public void run() {
//							count++;
							System.out.println("#FFFFFBBBBBBB    ID:   " + _id);
							myIntent.setClass(TabSQLite_PlaceUpdateFromDB.this, Tab_PlaceFetch.class);
							myIntent.putExtra("id", _id);
							myIntent.putExtra("latitude", _latitude);
							myIntent.putExtra("longitude", _longitude);
							startActivity(myIntent);
//						}
//					};
//					Timer timer = new Timer();
//					if (count > 10) {
//						count = 0;
//						timer.schedule(task, Delay);
//					}
				} else {
					while (c.moveToNext()) {
						System.out.println("#DDDDDBBBBBB    ID:   " + _id);
						String place = c.getString(0);
						String place_type = c.getString(1);
						myIntent.setClass(TabSQLite_PlaceUpdateFromDB.this, TabSQLite_StorePlaceToDB.class);
						myIntent.putExtra("id", _id);
						myIntent.putExtra("place", place);
						myIntent.putExtra("place_type", place_type);
						startActivity(myIntent);
					}
				}
			}
			db.close();
		} catch (Exception e) {
		}
		return true;
	}

	public class asyncTaskProgress extends AsyncTask<Void, Integer, Void> {

		ProgressDialog progressDialog;

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			TabSQLite_PlaceUpdateFromDB.this.finish();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog = new ProgressDialog(TabSQLite_PlaceUpdateFromDB.this);
			progressDialog.setTitle("Records Updating!!");
			progressDialog.setMessage("Please wait!!\n It may take few minutes to update");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					dialog.cancel();
					TabSQLite_PlaceUpdateFromDB.this.finish();
				}
			});
			progressDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Complete = getMatchInDB();
			while (!Complete) {
				SystemClock.sleep(20000000);
			}
			return null;
		}
	}
}
