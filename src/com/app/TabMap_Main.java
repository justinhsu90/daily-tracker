package com.app;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TabMap_Main extends MapActivity implements LocationListener {
	Boolean LocationEnable;
	private LocationManager locationManager;
	static MapView mapView;
	private MapController mapController;
	private MyLocationOverlay mylayer;
	String bestProvider;
	public TabSQLite_SQLiteHelper helper = new TabSQLite_SQLiteHelper(this);
	NumberFormat NF = NumberFormat.getInstance();
	public int UpdateFrequency;
	SharedPreferences setting_data;
	ToggleButton locationButton;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_main);
		LocationEnable=Tab_StartPage.LocationE;

		/** initial buttons for satellite view and map view **/
		Button mSat = (Button) findViewById(R.id.sat);
		mSat.setOnClickListener(mSat_listener);
		Button mMap = (Button) findViewById(R.id.map);
		mMap.setOnClickListener(mMap_listener);
		locationButton = (ToggleButton) findViewById(R.id.enable_location_button);
		locationButton.setOnClickListener(LocationButtonListener);
		/** run the map setup **/
		MapSetup();
		MapUpdate();
		mylayer = new MyLocationOverlay(this, mapView);
		mylayer.enableCompass();

	}

	OnClickListener LocationButtonListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (locationButton.isChecked()) {
				locationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_icon_mylocation_off));
				enableMyLocation(true);
			} else {
				locationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_icon_mylocation_on));
				locationButton.setChecked(false);
				enableMyLocation(false);
			}
		}
	};

	/** satellite view button listener **/
	private Button.OnClickListener mSat_listener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			mapView.setSatellite(true);
		}

	};
	/** map view button listener **/
	private Button.OnClickListener mMap_listener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			mapView.setSatellite(false);
		}
	};

	/** setup the map view **/
	private void MapSetup() {
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(16);
	}

	public void LocationSetup(Boolean boo) {
		if (boo) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			bestProvider = locationManager.getBestProvider(getCriteria(), true);
			locationManager.requestLocationUpdates(bestProvider, 0, 0, TabMap_Main.this);
			enableMyLocation(true);
		} else {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(this);
			enableMyLocation(false);
		}
	}

	/** setup the map view **/
	private void enableMyLocation(Boolean boo) {
		List<Overlay> overlays = mapView.getOverlays();
		if (boo) {
			mylayer.enableMyLocation();
			mylayer.runOnFirstFix(new Runnable() {
				@Override
				public void run() {
					mapController.animateTo(mylayer.getMyLocation());
				}
			});
			overlays.add(mylayer);
		} else {
			mylayer.disableMyLocation();
		}

	}

	/** set the requirement for GPS,Cell Network,WIFI **/
	public Criteria getCriteria() {
		Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(true);
		criteria.setCostAllowed(false);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationEnable=Tab_StartPage.LocationE;
		SharedPreferences setting_data;
		setting_data = getSharedPreferences("setting", 0);
		UpdateFrequency = setting_data.getInt("Update_Frequency", 120000);
		LocationSetup(LocationEnable);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		startindex = -1;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	int count = 0;
	boolean timerboolean = true;

	public void countdown() {
		timerboolean = false;
		new CountDownTimer(UpdateFrequency, 1000) {
			@Override
			public void onFinish() {
				timerboolean = true;
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// System.out.println("#seconds remaining:" +
				// millisUntilFinished / 1000);
			}
		}.start();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (timerboolean) {
			NF.setMaximumFractionDigits(5);
			// Create points for coords
			double LAT = location.getLatitude();
			String Lat = NF.format(LAT);
			double lat = Double.valueOf(Lat);
			double LON = location.getLongitude();
			String Lon = NF.format(LON);
			double lon = Double.valueOf(Lon);
			Toast.makeText(getApplicationContext(), "LAT:  " + String.valueOf(lat), Toast.LENGTH_SHORT).show();
			// If location has been found
			if (location != null) {
				// Store lat point
				lat = lat * 1E6;
				lon = lon * 1E6;
				// Create geo point from lat and lon
				GeoPoint point = new GeoPoint((int) lat, (int) lon);
				Toast.makeText(getApplicationContext(), "LAT in loop:  " + String.valueOf(lat), Toast.LENGTH_SHORT).show();
				// Change icon location and center map
				mapController.animateTo(point);
				mapController.setCenter(point);
				// Current location
				mapView.getOverlays().add(mylayer);
				mapView.postInvalidate();
//				if (count >= 5) {
				System.out.print("HHHHHHHHHHHHHHHHHHHHHHHHHH");
					helper.addInfo((long) 0, String.valueOf(LAT), String.valueOf(LON), "Not Available", "Not Available");

					MapUpdate();
//				} else {
//					count++;
//				}
			}
			countdown();
		}
	}

	/***************************************************************************************************************************************/
	int startindex = -1;
	int lastindex = 0;
	double startLat;
	double startLon;
	SQLiteDatabase db;

	public void MapUpdate() {
		if (startindex != -1) {
			update(lastindex);
		} else {
			getFirstInDB();
			update(lastindex);
		}
	}

	public void getFirstInDB() {
		db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select ID,LATITUDE,LONGITUDE from " + TabSQLite_SQLiteHelper.TABLE_NAME + " where TIME >=" + TodayInMillisecond()
				+ " limit 1", null);

		while (cursor.moveToNext()) {
			startindex = cursor.getInt(cursor.getColumnIndex("ID"));
			startLat = Double.valueOf(cursor.getString(cursor.getColumnIndex("LATITUDE")));
			startLon = Double.valueOf(cursor.getString(cursor.getColumnIndex("LONGITUDE")));
			lastindex = startindex;
		}
	}

	public void update(int start) {
		Cursor cursor = db.rawQuery("select ID,TIME,LATITUDE,LONGITUDE,PLACE,PLACE_TYPE from " + TabSQLite_SQLiteHelper.TABLE_NAME + " where TIME >="
				+ TodayInMillisecond() + " and ID>=" + start, null);
		while (cursor.moveToNext()) {
			GeoPoint pointfrom;
			double templat = startLat;
			double templon = startLon;
			templat = templat * 1E6;
			templon = templon * 1E6;
			pointfrom = new GeoPoint((int) templat, (int) templon);
			GeoPoint pointto;
			double newlat = Double.valueOf(cursor.getString(2));
			double newlon = Double.valueOf(cursor.getString(3));
			newlat = newlat * 1E6;
			newlon = newlon * 1E6;
			pointto = new GeoPoint((int) newlat, (int) newlon);
			mapView.getOverlays().add(new TabMap_RouteOverlay(pointfrom, pointto, TabMap_Main.this));
			Drawable drawable = this.getResources().getDrawable(R.drawable.map_point);
			TabMap_PointOverlay poverlay = new TabMap_PointOverlay(drawable, this);
			OverlayItem overlayitem = new OverlayItem(pointto, "Location Info", "ID: " + String.valueOf(cursor.getString(0)) + "\nTIME: "
					+ df.format(new Date(cursor.getLong(1))) + "\nLATITUDE: " + cursor.getString(2) + "\nLONGITUDE: " + cursor.getString(3) + "\nPLACE: "
					+ cursor.getString(4) + "\nPLACE TYPE: " + cursor.getString(5));
			poverlay.addOverlay(overlayitem);
			mapView.getOverlays().add(poverlay);

			startLat = Double.valueOf(cursor.getString(cursor.getColumnIndex("LATITUDE")));
			startLon = Double.valueOf(cursor.getString(cursor.getColumnIndex("LONGITUDE")));
			lastindex = cursor.getInt(cursor.getColumnIndex("ID"));
		}
	}

	public long TodayInMillisecond() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, c.get(Calendar.YEAR));
		c.set(Calendar.MONTH, c.get(Calendar.MONTH));
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DATE));
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		/** 43200 is 12 hour **/
		long time = (c.getTimeInMillis() / 1000);
		time = time * 1000;
		return time;

	}

	/***************************************************************************************************************************************/

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}