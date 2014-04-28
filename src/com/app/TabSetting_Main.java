package com.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class TabSetting_Main extends Activity {

	public ListView FB_list;
	public ListView History_list;
	public ListView Location_list;
	ArrayList<HashMap<String, String>> FBListData = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> HistoryListData = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> LocationListData = new ArrayList<HashMap<String, String>>();
	TabSQLite_SQLiteHelper helper;
	private SharedPreferences setting_data;
	private SharedPreferences.Editor data_editor;
	public SimpleAdapter FBlistAdapter;
	public SimpleAdapter HistorylistAdapter;
	public SimpleAdapter LocationlistAdapter;
	/** content to display **/
	String[] FBlist = { "Facebook Login" };
	// "Place-Fetch with 3G"
	String[] FBsubList = { "Login to use Check-in of Facebook" };
	// "Fetch-places details by using 3G Network"
	String[] Historylist = { "Database Reset" };
	String[] HistorysubList = { "Remove all the records in application" };
	String[] Locationlist = { "Update Frequency" };
	String[] LocationsubList = { "The frequency of GPS updating" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_main);
		ShowList();
		Button exit_button = (Button) findViewById(R.id.program_exit_button);
		exit_button.setOnClickListener(exitClickListener);
		setting_data = getSharedPreferences("setting", 0);
		data_editor = setting_data.edit();
		FB_list.setOnItemClickListener(FBClick);
		History_list.setOnItemClickListener(HistoryClick);
		Location_list.setOnItemClickListener(LocationClick);
		helper = new TabSQLite_SQLiteHelper(this);
	}

	/** listener for termination of application **/
	Button.OnClickListener exitClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder alert = new AlertDialog.Builder(TabSetting_Main.this);
			alert.setTitle("WARNING!!");
			alert.setMessage("Do you want to terminate the application?");
			alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialoginterface, int i) {
					System.exit(0);
				}
			});
			alert.setNegativeButton("NO", null);
			alert.show();
		}
	};

	/** set the list view for each settings **/
	public void ShowList() {
		FB_list = (ListView) findViewById(R.id.listView1);
		History_list = (ListView) findViewById(R.id.listView2);
		Location_list = (ListView) findViewById(R.id.listView3);

		FBlistAdapter = new SimpleAdapter(this, FBListData, R.layout.setting_list_item, new String[] { "title", "subtitle" }, new int[] { R.id.title,
				R.id.sub_title });
		HistorylistAdapter = new SimpleAdapter(this, HistoryListData, R.layout.setting_list_item, new String[] { "title", "subtitle" }, new int[] { R.id.title,
				R.id.sub_title });
		LocationlistAdapter = new SimpleAdapter(this, LocationListData, R.layout.setting_list_item, new String[] { "title", "subtitle" }, new int[] {
				R.id.title, R.id.sub_title });
		FB_list.setAdapter(FBlistAdapter);
		History_list.setAdapter(HistorylistAdapter);
		Location_list.setAdapter(LocationlistAdapter);
		int looptime;
		if (FBlist.length >= Historylist.length) {
			if (FBlist.length >= Locationlist.length) {
				looptime = FBlist.length;
			} else {
				looptime = Locationlist.length;
			}
		} else {
			if (Historylist.length >= Locationlist.length) {
				looptime = Historylist.length;
			} else {
				looptime = Locationlist.length;
			}
		}
		for (int i = 0; i < looptime; i++) {
			if (i < FBlist.length) {
				HashMap<String, String> FBmap = new HashMap<String, String>();
				FBmap.put("title", FBlist[i]);
				FBmap.put("subtitle", FBsubList[i]);
				FBListData.add(FBmap);
			}
			if (i < Historylist.length) {
				HashMap<String, String> Historymap = new HashMap<String, String>();
				Historymap.put("title", Historylist[i]);
				Historymap.put("subtitle", HistorysubList[i]);
				HistoryListData.add(Historymap);
			}
			if (i < Locationlist.length) {
				HashMap<String, String> Locationmap = new HashMap<String, String>();
				Locationmap.put("title", Locationlist[i]);
				Locationmap.put("subtitle", LocationsubList[i]);
				LocationListData.add(Locationmap);
			}
		}
	}

	/** listener for facebook-relatived settings **/
	OnItemClickListener FBClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0: {
				final Intent myIntent = new Intent();
				myIntent.setClass(TabSetting_Main.this, FB_FB.class);
				startActivity(myIntent);
				break;
			}
			}
		}
	};
	/** listener for history(Database)-relatived settings **/
	OnItemClickListener HistoryClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0: {
				AlertDialog.Builder alert = new AlertDialog.Builder(TabSetting_Main.this);
				alert.setTitle("WARNING!!");
				alert.setMessage("Do you want to remove all records on the mobile?");
				alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						helper.DeleteTable(TabSQLite_SQLiteHelper.TABLE_NAME);
						helper.CreateTable(TabSQLite_SQLiteHelper.TABLE_NAME);
						AlertDialog.Builder alert1 = new AlertDialog.Builder(TabSetting_Main.this);
						alert1.setTitle("REMOVED SUCCESSFULLY!!");
						alert1.setMessage("All records have been removed");
						alert1.setPositiveButton("DONE", null);
						alert1.show();
					}
				});
				alert.setNegativeButton("NO", null);
				alert.show();
				break;
			}
			}
		}
	};
	/** listener for location-relatived settings **/
	OnItemClickListener LocationClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0: {
				AlertDialog.Builder picker = new AlertDialog.Builder(TabSetting_Main.this);
				picker.setTitle("Update Frequency");
				picker.setMessage("please select period to update location\n Between 5 to 300 seconds\ncurrent update period is: "
						+ setting_data.getInt("Update_Frequency", 120000) / 1000 + " seconds.");
				picker.setIcon(android.R.drawable.ic_dialog_info);
				final EditText input = new EditText(TabSetting_Main.this);
				input.setInputType(InputType.TYPE_CLASS_NUMBER);
				picker.setView(input);
				picker.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						try {
							int time = Integer.valueOf(String.valueOf(input.getText()));
							if (time >= 5 && time <= 300) {
								data_editor.putInt("Update_Frequency", time * 1000);
								data_editor.commit();
							} else {
								AlertDialog.Builder alert1 = new AlertDialog.Builder(TabSetting_Main.this);
								alert1.setTitle("WARNING");
								alert1.setMessage("Please input proper second");
								alert1.setPositiveButton("DONE", null);
								alert1.show();
							}
						} catch (Exception e) {
							AlertDialog.Builder alert1 = new AlertDialog.Builder(TabSetting_Main.this);
							alert1.setTitle("WARNING");
							alert1.setMessage("Please input proper second");
							alert1.setPositiveButton("DONE", null);
							alert1.show();
						}
					}
				});
				picker.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						data_editor.putInt("Update_Frequency", setting_data.getInt("Update_Frequency", 120000));
						data_editor.commit();
					}
				});
				picker.show();
				break;
			}
			}
		}
	};

}
