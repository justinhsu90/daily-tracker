package com.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class TabSQLite_Main extends Activity {
	private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss");
	public ListView list;
	private TabSQLite_SQLiteHelper helper;
	ArrayList<HashMap<String, Object>> listData;
	public SimpleAdapter listItemAdapter;
	ArrayList<HashMap<String, String>> ListData = new ArrayList<HashMap<String, String>>();
	ArrayAdapter<String> adapter;
	final String[] DBoptionList = new String[] { "Today", "Yesterday", "All", "Past 3 days", "past 7 days", "Past 30 days", "Search" };
	String[] search_list = new String[] { "PLACE", "PLACE TYPE" };
	public Calendar calendar = Calendar.getInstance();
	public int SpinnerDefault = 0;
	public int SelectItem = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_list_main);
		this.helper = new TabSQLite_SQLiteHelper(this);
		/** list view **/
		list = (ListView) findViewById(R.id.list_items);

	}

	/** setup list view for Database display **/
	public void ListView(int position) {
		listItemAdapter = new SimpleAdapter(TabSQLite_Main.this, listData, R.layout.db_list_item, new String[] { "id", "time", "latitude", "longitude",
				"place", "place_type" }, new int[] { R.id.id, R.id.time, R.id.latitude, R.id.longitude, R.id.place, R.id.place_type });

		list.setAdapter(listItemAdapter);
		list.setSelectionFromTop(position, 0);

		list.setOnCreateContextMenuListener(listviewLongPress);
		list.setOnItemClickListener(listClick);
	}

	/** spinner to select different output of list view **/
	public void Spin(int DefaultSelection, int Selected) {
		final int SelectedItem = Selected;

		Spinner spinner = (Spinner) findViewById(R.id.db_spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DBoptionList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(DefaultSelection);

		spinner.setPrompt("Please select one from following options ");
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long id) {
				SpinnerDefault = position;

				switch (position) {
				// TODO Auto-generated method stub
				/** select TODAY **/
				case 0: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getAllData(TabSQLite_SQLiteHelper.TABLE_NAME, "where TIME>=" + start, null);
					ListView(SelectedItem);
					break;
				}
				/** select YESTERDAY **/
				case 1: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getAllData(TabSQLite_SQLiteHelper.TABLE_NAME, "Where TIME >=" + start + " and TIME<" + end, null);
					ListView(SelectedItem);
					break;
				}
				/** select ALL **/
				case 2: {
					getAllData(TabSQLite_SQLiteHelper.TABLE_NAME, "", null);
					ListView(SelectedItem);
					break;
				}
				/** select of past 3 days **/
				case 3: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 3, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getAllData(TabSQLite_SQLiteHelper.TABLE_NAME, "Where TIME >=" + start + " and TIME<" + end, null);
					ListView(SelectedItem);
					break;
				}
				/** select of past 7 days **/
				case 4: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 7, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getAllData(TabSQLite_SQLiteHelper.TABLE_NAME, "Where TIME >=" + start + " and TIME<" + end, null);
					ListView(SelectedItem);
					break;
				}
				/** select of past 30 days **/
				case 5: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 30, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getAllData(TabSQLite_SQLiteHelper.TABLE_NAME, "Where TIME >=" + start + " and TIME<" + end, null);
					ListView(SelectedItem);
					break;
				}
				/** search for place or place type **/
				case 6: {

					AlertDialog.Builder choose = new AlertDialog.Builder(TabSQLite_Main.this);
					choose.setTitle("Which to search?");
					choose.setItems(search_list, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, final int which) {
							AlertDialog.Builder alert = new AlertDialog.Builder(TabSQLite_Main.this);
							String[] list = { "PLACE", "PLACE TYPE" };
							alert.setTitle("Searching for " + list[which]);
							alert.setMessage("please type in to search");
							final EditText input2 = new EditText(TabSQLite_Main.this);
							alert.setView(input2);
							alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialoginterface, int i) {
									switch (which) {
									case 0:
										getAllData(TabSQLite_SQLiteHelper.TABLE_NAME, "where PLACE = ?", new String[] { String.valueOf(input2.getText()) });
										ListView(SelectedItem);
										break;
									case 1:
										getAllData(TabSQLite_SQLiteHelper.TABLE_NAME, "where PLACE_TYPE = ?", new String[] { String.valueOf(input2.getText()) });
										ListView(SelectedItem);
										break;
									}
								};
							});
							alert.setNegativeButton("Discard", null);
							alert.show();
						}
					});
					choose.setNegativeButton("Cancel", null);
					choose.show();
				}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
	}

	/** convert time to Unix string **/
	public long TimeToUnix(int year, int month, int day, int hour, int minute, int second) {

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, 0);
		long time = (c.getTimeInMillis() / 1000) - 43200;
		time = time * 1000;
		return time;
	}

	/** display the content in Database **/
	public void getAllData(String table, String Query2, String[] Query3) {
		try {
			String order = " order by ID desc";
			String query = "select * from " + table + " " + Query2 + order;
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.rawQuery(query, Query3);

			listData = new ArrayList<HashMap<String, Object>>();
			while (cursor.moveToNext()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", String.valueOf(cursor.getString(0)));
				map.put("time", df.format(new Date(cursor.getLong(1))));

				map.put("latitude", cursor.getString(2));
				map.put("longitude", cursor.getString(3));
				map.put("place", cursor.getString(4));
				map.put("place_type", cursor.getString(5));
				map.put("place_get", cursor.getString(6));
				listData.add(map);
			}
		} catch (Exception e) {

		}
	}

	/** delete the item in Database **/
	public boolean delete(SQLiteDatabase mDb, String table, int id) {
		String whereClause = "id=?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		try {
			mDb.delete(table, whereClause, whereArgs);
		} catch (SQLException e) {
			Toast.makeText(getApplicationContext(), "fail to remove", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	int scrolledX = 0;

	/** display detail of item **/
	OnItemClickListener listClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			String CurrentID = filtID(parent.getItemAtPosition(position).toString());
			SelectItem = list.getFirstVisiblePosition();

			final Intent myIntent = new Intent();
			myIntent.setClass(TabSQLite_Main.this, TabSQLite_DBSingleItem.class);
			myIntent.putExtra("id", CurrentID);
			startActivity(myIntent);
		}
	};

	/** get the ID of the item clicked **/
	public String filtID(String string) {
		String result = "";
		Pattern pattern = Pattern.compile("id=\\d+,");
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			result = string.substring(matcher.start() + 3, matcher.end() - 1);
		}
		return result;
	}

	/** setup for long click **/
	OnCreateContextMenuListener listviewLongPress = new OnCreateContextMenuListener() {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			final SQLiteDatabase db = helper.getReadableDatabase();

			// TODO Auto-generated method stub
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			new AlertDialog.Builder(TabSQLite_Main.this).setTitle("WARNING--REMOVE").setIcon(android.R.drawable.ic_dialog_info)
					.setMessage("do you want to remove this record?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialoginterface, int i) {
							int mListPos = info.position;
							HashMap<String, Object> map = listData.get(mListPos);
							int id = Integer.valueOf((map.get("id").toString()));
							if (delete(db, TabSQLite_SQLiteHelper.TABLE_NAME, id)) {
								listData.remove(mListPos);
								listItemAdapter.notifyDataSetChanged();
							}
						}
					}).setNegativeButton("NO", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialoginterface, int i) {

						}
					}).show();
		}

	};

	/** menu function **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(1, 0, 0, "Batch Remove").setIcon(R.drawable.menu_remove);
		// menu.add(0, 1, 1, "Batch Edit").setIcon(R.drawable.menu_edit);
		menu.add(1, 2, 2, "To Top").setIcon(R.drawable.menu_arrow_green);
		menu.add(1, 3, 3, "To Bottom").setIcon(R.drawable.menu_arrow_red);
		return super.onCreateOptionsMenu(menu);
	}

	/** on select listener of menu function **/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:

			break;
		case 1:

			break;
		case 2:
			list.setSelectionAfterHeaderView();
			break;
		case 3:
			list.setSelection(listItemAdapter.getCount() - 1);

			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Spin(SpinnerDefault, SelectItem);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}