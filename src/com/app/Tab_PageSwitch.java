package com.app;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Tab_PageSwitch extends TabActivity {
	TabHost tabHost;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);

		Resources res = getResources(); // Resource object to get Drawables
		tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab


		intent = new Intent().setClass(this, Tab_StartPage.class);
		spec = tabHost.newTabSpec("Tab1").setIndicator(null, res.getDrawable(R.drawable.tab_icon_home)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, TabSQLite_Main.class);
		spec = tabHost.newTabSpec("Tab2").setIndicator(null, res.getDrawable(R.drawable.tab_icon_history)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, TabMap_Main.class);
		spec = tabHost.newTabSpec("Tab3").setIndicator(null, res.getDrawable(R.drawable.tab_icon_maps)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Result_Main.class);
		spec = tabHost.newTabSpec("Tab6").setIndicator(null, res.getDrawable(R.drawable.tab_icon_result)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, TabSetting_Main.class);
		spec = tabHost.newTabSpec("Tab5").setIndicator(null, res.getDrawable(R.drawable.tab_icon_setting)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

	public TabHost switchTab(int index) {
		
		return tabHost;
	}

}