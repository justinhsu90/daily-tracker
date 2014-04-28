package com.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TabSQLite_DBSingleItem extends MapActivity {
	private String ID;
	String[] edit_list = new String[] { "Place Edit", "Batch Place Edit", "Place Type Edit", "Batch Place Type Edit", "Batch Place Remove",
			"Batch Place Type Remove" };
	private TabSQLite_SQLiteHelper helper = new TabSQLite_SQLiteHelper(this);

	ArrayList<HashMap<String, Object>> listData;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	SQLiteDatabase db;
	private MapView mapView;
	private MapController mapController;
	private GeoPoint geoPoint;
	private MyLocationOverlay MLO;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_single_item_display);
		Bundle extras = getIntent().getExtras();
		ID = extras.getString("id");
		mapView = (MapView) findViewById(R.id.mapView);
		Button mSat = (Button) findViewById(R.id.sat);
		mSat.setOnClickListener(mSat_listener);
		Button mMap = (Button) findViewById(R.id.map);
		mMap.setOnClickListener(mMap_listener);
		mapController = mapView.getController();
		MLO = new MyLocationOverlay(this, mapView);
		MLO.enableCompass();
		MLO.enableMyLocation();
		mapView.setSatellite(false);
		mapView.setStreetView(true);
		mapController.setZoom(18);
	}

	private Button.OnClickListener mSat_listener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			mapView.setSatellite(true);
		}
	};

	private Button.OnClickListener mMap_listener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			mapView.setSatellite(false);
		}
	};

	public void mapDisplay(double lat, double lon) {
		geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		mapView.displayZoomControls(true);
		mapController.animateTo(geoPoint);
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		mapView.getOverlays().add(MLO);
		listOfOverlays.add(mapOverlay);
	}

	int _id = -1;
	long _time = 0;
	String _latitude = "0";
	String _longitude = "0";
	String _place = "";
	String _place_type = "";
	int _place_get = -1;

	public void display() {
		db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TabSQLite_SQLiteHelper.TABLE_NAME + " where ID='" + ID + "'", null);
		while (cursor.moveToNext()) {
			_id = cursor.getInt(cursor.getColumnIndex("ID"));
			_time = cursor.getLong(cursor.getColumnIndex("TIME"));
			_latitude = cursor.getString(cursor.getColumnIndex("LATITUDE"));
			_longitude = cursor.getString(cursor.getColumnIndex("LONGITUDE"));
			_place = cursor.getString(cursor.getColumnIndex("PLACE"));
			_place_type = cursor.getString(cursor.getColumnIndex("PLACE_TYPE"));
			_place_get = cursor.getInt(cursor.getColumnIndex("PLACE_GET"));
		}
		mapDisplay(Double.parseDouble(_latitude), Double.parseDouble(_longitude));

		TextView TVid = (TextView) findViewById(R.id.id);
		TextView TVtime = (TextView) findViewById(R.id.time);
		TextView TVlatitude = (TextView) findViewById(R.id.latitude);
		TextView TVlongitude = (TextView) findViewById(R.id.longitude);
		TextView TVplace = (TextView) findViewById(R.id.place);
		TextView TVplace_type = (TextView) findViewById(R.id.place_type);

		TVid.setText(String.valueOf(_id));
		TVtime.setText(df.format(new Date(_time)));
		TVlatitude.setText(_latitude);
		TVlongitude.setText(_longitude);
		TVplace.setText(_place);
		TVplace_type.setText(_place_type);
	}

	public class MapOverlay extends com.google.android.maps.Overlay {
		@SuppressLint("ResourceAsColor")
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
			super.draw(canvas, mapView, shadow);
			Projection projection = mapView.getProjection();
			// —translate the GeoPoint to screen pixels—
			Point screenPts = new Point();
			projection.toPixels(geoPoint, screenPts);
			// —add paint—
			Paint paint = new Paint();
			paint.setColor(R.color.black);
			// 設定文字大小
			paint.setTextSize(20);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			// —add the first marker—
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin);
			canvas.drawBitmap(bmp, screenPts.x - 7, screenPts.y, null);
			canvas.drawText("", screenPts.x - 50, screenPts.y, paint);

			return true;
		}
	}

	public void editDialog(final int which) {
		db = helper.getReadableDatabase();

		AlertDialog.Builder alert = new AlertDialog.Builder(TabSQLite_DBSingleItem.this);
		if (which < 4) {
			alert.setTitle("Editting");
			alert.setMessage("Please insert " + edit_list[which] + " :");
			final EditText input = new EditText(TabSQLite_DBSingleItem.this);
			alert.setView(input);
			alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialoginterface, int i) {
					Toast.makeText(getApplicationContext(), "input:  " + input.getText(), Toast.LENGTH_LONG).show();
					switch (which) {
					/** place edit **/
					case 0:
						db.execSQL("update " + TabSQLite_SQLiteHelper.TABLE_NAME + " set PLACE= '" + String.valueOf(input.getText()) + "' where ID= "
								+ Integer.valueOf(ID));
						break;
					/** batch place edit **/
					case 1:
						db.execSQL("update " + TabSQLite_SQLiteHelper.TABLE_NAME + " set PLACE= '" + String.valueOf(input.getText()) + "' where LATITUDE= "
								+ _latitude + " and LONGITUDE= " + _longitude);
						break;
					/** place type edit **/
					case 2:
						db.execSQL("update " + TabSQLite_SQLiteHelper.TABLE_NAME + " set PLACE_TYPE= '" + String.valueOf(input.getText()) + "' where ID= "
								+ Integer.valueOf(ID));
						break;
					/** batch place type edit **/
					case 3:
						db.execSQL("update " + TabSQLite_SQLiteHelper.TABLE_NAME + " set PLACE_TYPE= '" + String.valueOf(input.getText()) + "' where PLACE= '"
								+ _place + "'");
						break;
					}
					display();
				}
			});
			alert.setNegativeButton("Discard", null);
			alert.show();
		} else {
			AlertDialog.Builder RemoveAlert = new AlertDialog.Builder(TabSQLite_DBSingleItem.this);
			String string;
			if (which == 4) {
				string = "PLACE";
			} else {
				string = "PLACE TYPE";
			}
			RemoveAlert.setTitle("Warning--Remove");
			RemoveAlert.setMessage("All the records of current " + string + " will be removed.\nAre you sure to remove all?");
			RemoveAlert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialoginterface, int i) {
					switch (which) {
					case 4:
						db.execSQL("delete from " + TabSQLite_SQLiteHelper.TABLE_NAME + " where PLACE= '" + _place + "'");
						TabSQLite_DBSingleItem.this.finish();
						break;
					case 5:
						db.execSQL("delete from " + TabSQLite_SQLiteHelper.TABLE_NAME + " where PLACE_TYPE= '" + _place_type + "'");
						TabSQLite_DBSingleItem.this.finish();
						break;
					}
				}
			});
			RemoveAlert.setNegativeButton("NO", null);
			RemoveAlert.show();
		}
	}

	/** menu function **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Place Edit").setIcon(R.drawable.menu_place_edit);
		menu.add(0, 1, 1, "Batch Place Edit").setIcon(R.drawable.menu_batch_place_edit);
		menu.add(0, 2, 2, "Place Type Edit").setIcon(R.drawable.menu_place_type_edit);
		menu.add(0, 3, 3, "Batch Place Type Edit").setIcon(R.drawable.menu_batch_place_type_edit);
		menu.add(0, 4, 4, "Batch Place Remove").setIcon(R.drawable.menu_batch_place_remove);
		menu.add(0, 5, 5, "Batch Place Type Remove").setIcon(R.drawable.menu_batch_place_type_remove);

		return super.onCreateOptionsMenu(menu);
	}

	/** on select listener of menu function **/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		editDialog(item.getItemId());
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		display();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
