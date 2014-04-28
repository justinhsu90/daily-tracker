package com.app;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class Tab_StartPage extends Activity {
	public static Boolean LocationE;
	TabSQLite_SQLiteHelper helper;
	Tab_PlaceFetch PF;
	ToggleButton schedule_location_fetch_button;
	ToggleButton schedule_place_fetch_button;
	ToggleButton location_fetch_button;
	ToggleButton place_fetch_button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_page_main);
		this.helper = new TabSQLite_SQLiteHelper(this);
		/** button for location fetch **/
		location_fetch_button = (ToggleButton) findViewById(R.id.location_fetch_button);
		location_fetch_button.setOnClickListener(location_fetch_listener);
		/** button for place fetch **/
		place_fetch_button = (ToggleButton) findViewById(R.id.place_fetch_button);
		place_fetch_button.setOnClickListener(place_fetch_listener);
		LocationE = false;
	}

	/** click listener for location fetch **/

	OnClickListener location_fetch_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (location_fetch_button.isChecked()) {
				location_fetch_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.togglebutton_red));

				LocationE = true;
				TabActivity tab = (TabActivity) Tab_StartPage.this.getParent();
				tab.getTabHost().setCurrentTab(2);
			} else {
				location_fetch_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.togglebutton_green));
				LocationE = false;
				TabActivity tab = (TabActivity) Tab_StartPage.this.getParent();
				tab.getTabHost().setCurrentTab(2);

			}

		}
	};
	/** click listener for place fetch **/
	OnClickListener place_fetch_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				if (FB_Utility.mFacebook.isSessionValid()) {

					final Intent myIntent = new Intent();
					myIntent.setClass(Tab_StartPage.this, TabSQLite_PlaceUpdateFromDB.class);
					startActivity(myIntent);

				}
			} catch (Exception e) {
				final Intent myIntent = new Intent();
				myIntent.setClass(Tab_StartPage.this, FB_FB.class);
				startActivity(myIntent);
			}

		}
	};

}
