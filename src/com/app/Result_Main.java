package com.app;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class Result_Main extends Activity {
	private TabSQLite_SQLiteHelper helper;
	SQLiteDatabase db;
	public ListView PieList;
	public ListView BarList;

	public SimpleAdapter PieListAdapter;
	public SimpleAdapter BarListAdapter;

	ArrayList<HashMap<String, Object>> PieListData;
	ArrayList<HashMap<String, Object>> BarListData;

	ArrayAdapter<String> adapter;
	List<Result_PieDetailsItem> piedata = new ArrayList<Result_PieDetailsItem>(0);
	private XYMultipleSeriesDataset ds;
	private XYMultipleSeriesRenderer renderer;
	private GraphicalView gv;
	private XYSeriesRenderer xyRender;
	Calendar calendar = Calendar.getInstance();
	ArrayList<String> PLACE = new ArrayList<String>();
	ArrayList<Integer> PLACE_COUNT = new ArrayList<Integer>();
	ArrayList<String> PLACE_TYPE = new ArrayList<String>();
	ArrayList<Integer> PLACE_TYPE_COUNT = new ArrayList<Integer>();
	String[] PiechartoptionList = new String[] { "Time Spent Today", "Place Went Today", "Time Spent Yesterday", "Place Went Yesterday",
			"Time Spent in past 3 days", "Place Went in past 3 days", "Time Spent in past 7 days", "Place Went in past 7 days", "Time Spent in past 30 days",
			"Place Went in past 30 days", "Time Spent in All", "Place Went in All" };
	String[] LinechartoptionList = new String[] { "Today compared to Yesterday", "Today compared to past 3 days", "Today compared to past 7 days",
			"Today compared to past 30 days", "Yesterday compared to past 3 days", "Yesterday compared to past 7 days", "Yesterday compared to past 30 days" };
	NumberFormat NF = NumberFormat.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.helper = new TabSQLite_SQLiteHelper(this);
		displayView(0);
	}

	/** switch to choose which chart to display **/
	public void displayView(int which) {
		switch (which) {
		case 0:
			piechartView();
			break;
		case 1:
			barchartView();
			break;
		}
	}

	/** main method to run the linechart view **/
	public void barchartView() {
		setContentView(R.layout.result_barchart);
		BarList = (ListView) findViewById(R.id.barchart_listview);

		Spinner spinner = (Spinner) findViewById(R.id.piechart_spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, LinechartoptionList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setPrompt("Please select one from following options ");
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				switch (position) {
				/** Today compared to Yesterday **/
				case 0: {
					long Todaystart = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					long Yesterdaystart = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1, 0, 0, 0);
					String[] TodayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "where TIME>=" + Todaystart, new String[] {});
					String[] YesterdayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + Yesterdaystart + " and TIME<"
							+ Todaystart, new String[] {});
					String[] LineName = new String[] { "Today", "Yesterday" };
					ArrayList<String[]> packages = new ArrayList<String[]>();
					packages.add(TodayDB);
					packages.add(YesterdayDB);
					String[][] DateData = packages.toArray(new String[packages.size()][]);
					ArrayList<String> Xlabel = genXlabel(DateData);
					double[][] Percentage = getPercentage(Xlabel, DateData);
					runView(Percentage, Xlabel, LineName);
					Barchart_listview(Percentage, Xlabel);
					break;
				}
				/** Today compared to past 3 days **/
				case 1: {
					long Todaystart = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					long Yesterdaystart = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 3, 0, 0, 0);
					String[] TodayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "where TIME>=" + Todaystart, new String[] {});
					String[] YesterdayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + Yesterdaystart + " and TIME<"
							+ Todaystart, new String[] {});
					String[] LineName = new String[] { "Today", "past 3 days" };
					ArrayList<String[]> packages = new ArrayList<String[]>();
					packages.add(TodayDB);
					packages.add(YesterdayDB);
					String[][] DateData = packages.toArray(new String[packages.size()][]);
					ArrayList<String> Xlabel = genXlabel(DateData);
					double[][] Percentage = getPercentage(Xlabel, DateData);
					runView(Percentage, Xlabel, LineName);
					Barchart_listview(Percentage, Xlabel);
					break;
				}
				/** Today compared to past 7 days **/
				case 2: {
					long Todaystart = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					long Yesterdaystart = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 7, 0, 0, 0);
					String[] TodayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "where TIME>=" + Todaystart, new String[] {});
					String[] YesterdayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + Yesterdaystart + " and TIME<"
							+ Todaystart, new String[] {});
					String[] LineName = new String[] { "Today", "past 7 days" };
					ArrayList<String[]> packages = new ArrayList<String[]>();
					packages.add(TodayDB);
					packages.add(YesterdayDB);
					String[][] DateData = packages.toArray(new String[packages.size()][]);
					ArrayList<String> Xlabel = genXlabel(DateData);
					double[][] Percentage = getPercentage(Xlabel, DateData);
					runView(Percentage, Xlabel, LineName);
					Barchart_listview(Percentage, Xlabel);
					break;
				}
				/** Today compared to past 30 days **/
				case 3: {
					long Todaystart = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					long Yesterdaystart = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 30, 0, 0, 0);
					String[] TodayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "where TIME>=" + Todaystart, new String[] {});
					String[] YesterdayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + Yesterdaystart + " and TIME<"
							+ Todaystart, new String[] {});
					String[] LineName = new String[] { "Today", "past 30 days" };
					ArrayList<String[]> packages = new ArrayList<String[]>();
					packages.add(TodayDB);
					packages.add(YesterdayDB);
					String[][] DateData = packages.toArray(new String[packages.size()][]);
					ArrayList<String> Xlabel = genXlabel(DateData);
					double[][] Percentage = getPercentage(Xlabel, DateData);
					runView(Percentage, Xlabel, LineName);
					Barchart_listview(Percentage, Xlabel);
					break;
				}
				/** Yesterday compared to past 3 days **/
				case 4: {
					long startday = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1, 0, 0, 0);
					long endday = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 3, 0, 0, 0);
					String[] TodayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "where TIME>=" + startday, new String[] {});
					String[] YesterdayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + endday + " and TIME<" + startday,
							new String[] {});
					String[] LineName = new String[] { "Yesterday", "past 3 days" };
					ArrayList<String[]> packages = new ArrayList<String[]>();
					packages.add(TodayDB);
					packages.add(YesterdayDB);
					String[][] DateData = packages.toArray(new String[packages.size()][]);
					ArrayList<String> Xlabel = genXlabel(DateData);
					double[][] Percentage = getPercentage(Xlabel, DateData);
					runView(Percentage, Xlabel, LineName);
					Barchart_listview(Percentage, Xlabel);
					break;
				}
				/** Yesterday compared to past 7 days **/
				case 5: {
					long startday = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1, 0, 0, 0);
					long endday = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 7, 0, 0, 0);
					String[] TodayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "where TIME>=" + startday, new String[] {});
					String[] YesterdayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + endday + " and TIME<" + startday,
							new String[] {});
					String[] LineName = new String[] { "Yesterday", "past 7 days" };
					ArrayList<String[]> packages = new ArrayList<String[]>();
					packages.add(TodayDB);
					packages.add(YesterdayDB);
					String[][] DateData = packages.toArray(new String[packages.size()][]);
					ArrayList<String> Xlabel = genXlabel(DateData);
					double[][] Percentage = getPercentage(Xlabel, DateData);
					runView(Percentage, Xlabel, LineName);
					Barchart_listview(Percentage, Xlabel);
					break;
				}
				/** Yesterday compared to past 30 days **/
				case 6: {
					long startday = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1, 0, 0, 0);
					long endday = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 30, 0, 0, 0);
					String[] TodayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "where TIME>=" + startday, new String[] {});
					String[] YesterdayDB = getfromDBBar(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + endday + " and TIME<" + startday,
							new String[] {});
					String[] LineName = new String[] { "Yesterday", "past 30 days" };
					ArrayList<String[]> packages = new ArrayList<String[]>();
					packages.add(TodayDB);
					packages.add(YesterdayDB);
					String[][] DateData = packages.toArray(new String[packages.size()][]);
					ArrayList<String> Xlabel = genXlabel(DateData);
					double[][] Percentage = getPercentage(Xlabel, DateData);
					runView(Percentage, Xlabel, LineName);
					Barchart_listview(Percentage, Xlabel);
					break;
				}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	/** run the view of barchart **/
	public void runView(double[][] percentage, ArrayList<String> Xlabel, String[] Linename) {

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		gv = ChartFactory.getBarChartView(this, getDataset(percentage, Xlabel, Linename), getRenderer(), null);
		layout.removeAllViews();
		layout.addView(gv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

	}

	/** get the specific date data from database and return in array **/
	public String[] getfromDBBar(String table, String type, String Query1, String[] Query2) {

		NF.setMaximumFractionDigits(2);
		ArrayList<String> GENERAL = new ArrayList<String>();
		ArrayList<Integer> GENERAL_COUNT = new ArrayList<Integer>();
		String result;
		String query = "select " + type + " from " + table + " " + Query1;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, Query2);
		while (cursor.moveToNext()) {
			result = cursor.getString(cursor.getColumnIndex(type));
			if (GENERAL.contains(result)) {
				int index = GENERAL.indexOf(result);
				int count = GENERAL_COUNT.get(index);
				count = count + 1;
				GENERAL_COUNT.set(GENERAL.indexOf(result), count);
			} else {
				GENERAL.add(result);
				int count = 1;
				GENERAL_COUNT.add(count);
			}
		}
		String itemslabel[] = GENERAL.toArray(new String[GENERAL.size()]);
		Integer items[] = GENERAL_COUNT.toArray(new Integer[GENERAL_COUNT.size()]);
		String[] forreturn = new String[GENERAL_COUNT.size() * 2];
		for (int i = 0; i < GENERAL_COUNT.size() * 2; i++) {
			if (i % 2 == 0) {
				forreturn[i] = itemslabel[i / 2];
			} else {
				String temp = NF.format(((double) items[i / 2] / cursor.getCount()) * 100);
				forreturn[i] = temp;
			}
		}
		return forreturn;
	}

	/**
	 * generate an arraylist which store all the data for certain barchart for
	 * displaying
	 **/
	public ArrayList<String> genXlabel(String[][] DateData) {
		ArrayList<String> Resultlabel = new ArrayList<String>();
		for (int i = 0; i < DateData.length; i++) {
			for (int j = 0; j < DateData[i].length; j = j + 2) {
				if (!Resultlabel.contains(DateData[i][j])) {
					Resultlabel.add(String.valueOf(DateData[i][j]));
				}
			}
		}
		return Resultlabel;
	}

	/** store the percentage of each records **/
	public double[][] getPercentage(ArrayList<String> Xlabel, String[][] DateData) {
		double[][] percentage = new double[Xlabel.size()][DateData.length];
		for (int i = 0; i < DateData.length; i++) {
			int k = 0;
			for (int j = 1; j < DateData[i].length; j = j + 2) {
				percentage[k][i] = Double.valueOf(DateData[i][j]);
				k++;
			}
		}
		return percentage;
	}

	/** Barchart List view **/
	public void Barchart_listview(double[][] percentage, ArrayList<String> Xlabel) {
		try {
			BarListData = new ArrayList<HashMap<String, Object>>();
			String[] label = Xlabel.toArray(new String[Xlabel.size()]);
			for (int i = 0; i < percentage.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();

				map.put("percentage1", percentage[i][0] + "%");
				map.put("percentage2", percentage[i][1] + "%");
				map.put("name", label[i]);
				BarListData.add(map);

			}
			BarListAdapter = new SimpleAdapter(Result_Main.this, BarListData, R.layout.result_barchart_listview_item, new String[] { "percentage1",
					"percentage2", "name" },
					new int[] { R.id.barchart_listview_percentage_1, R.id.barchart_listview_percentage_2, R.id.barchart_listview_name });

			BarList.setAdapter(BarListAdapter);
		} catch (Exception e) {
		}
	}

	/** draw the line on the chart **/
	private XYMultipleSeriesDataset getDataset(double[][] percentage, ArrayList<String> XLabel, String[] LineName) {

		renderer = new XYMultipleSeriesRenderer();
		ds = new XYMultipleSeriesDataset();
		// number of comparison lines (in pairs)
		renderer = new XYMultipleSeriesRenderer();
		for (int i = 0; i < percentage[0].length; i++) {
			CategorySeries series = new CategorySeries(LineName[i]);
			for (int k = 0; k < percentage.length; k++) {
				series.add(percentage[k][i]);
			}
			ds.addSeries(series.toXYSeries());
		}
		setRenderer(XLabel);
		setXY(percentage.length, XLabel);
		return ds;

	}

	/** set label of X axis **/
	public void setXY(int time, ArrayList<String> XLabel) {
		for (int i = 1; i <= time; i++) {
			renderer.addXTextLabel(i, XLabel.get(i - 1));
		}
	}

	/** return the renderer **/
	public XYMultipleSeriesRenderer getRenderer() {
		return renderer;

	}

	public void setRenderer(ArrayList<String> genXLabel) {
		// 新建一個xymultipleseries
		renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(25); // 設置座標軸標題文本大小
		renderer.setChartTitleTextSize(25); // 設置圖表標題文本大小
		renderer.setLabelsTextSize(25); // 設置軸標籤文本大小
		renderer.setLegendTextSize(30); // 設置圖例文本大小
		renderer.setPointSize(5f);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.GRAY);
		renderer.setMargins(new int[] { 10, 60, 50, 30 }); // 設置4邊留白
		renderer.setPanEnabled(false, false); // 設置x,y座標軸不會因使用者劃動螢幕而移動
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0));
		renderer.setBackgroundColor(Color.TRANSPARENT); // 設置背景色透明
		renderer.setApplyBackgroundColor(true); // 使背景色生效

		// // 設置一個系列的顏色為藍色
		xyRender = new XYSeriesRenderer();
		xyRender.setColor(Color.CYAN);
		xyRender.setDisplayChartValues(true);
		xyRender.setChartValuesTextSize(40);
		renderer.addSeriesRenderer(xyRender);
		// // 設置一個系列的顏色為red
		xyRender = new XYSeriesRenderer();
		xyRender.setColor(Color.RED);
		xyRender.setDisplayChartValues(true);
		xyRender.setChartValuesTextSize(40);
		renderer.addSeriesRenderer(xyRender);

		renderer.setXLabels(0);
		renderer.setYLabels(10);
		renderer.setPanEnabled(true, false);
		renderer.setBarSpacing(0.3f);
		renderer.setXTitle("Place Type"); // title of X direction
		renderer.setYTitle("Percentage");// title of Y direction
	}

	/*****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************/
	/** show the piechart display options by spinner **/
	public void piechartView() {
		setContentView(R.layout.result_piechart);
		PieList = (ListView) findViewById(R.id.piechart_listview);
		Spinner spinner = (Spinner) findViewById(R.id.piechart_spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PiechartoptionList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setPrompt("Please select one from following options ");
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				switch (position) {
				/** Time Spent for Today in place type **/
				case 0: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "where TIME>=" + start, null);
					drawPie(PLACE_TYPE, PLACE_TYPE_COUNT);
					PLACE_TYPE.clear();
					PLACE_TYPE_COUNT.clear();
					break;
				}
				/** Place went Today **/
				case 1: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE", "where TIME>=" + start, null);
					drawPie(PLACE, PLACE_COUNT);
					PLACE.clear();
					PLACE_COUNT.clear();
					break;
				}
				/** Time Spent for Yesterday in place type **/
				case 2: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + start + " and TIME<" + end, null);
					drawPie(PLACE_TYPE, PLACE_TYPE_COUNT);
					PLACE_TYPE.clear();
					PLACE_TYPE_COUNT.clear();
					break;
				}
				/** Place went yesterday **/
				case 3: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE", "Where TIME >=" + start + " and TIME<" + end, null);
					drawPie(PLACE, PLACE_COUNT);
					PLACE.clear();
					PLACE_COUNT.clear();
					break;
				}
				/** Time Spent past 3 days in place type **/
				case 4: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 3, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + start + " and TIME<" + end, null);
					drawPie(PLACE_TYPE, PLACE_TYPE_COUNT);
					PLACE_TYPE.clear();
					PLACE_TYPE_COUNT.clear();
					break;
				}
				/** Place went in past 3 days **/
				case 5: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 3, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE", "Where TIME >=" + start + " and TIME<" + end, null);
					drawPie(PLACE, PLACE_COUNT);
					PLACE.clear();
					PLACE_COUNT.clear();
				}
				/** Time Spent past 7 days in place type **/
				case 6: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 7, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + start + " and TIME<" + end, null);
					drawPie(PLACE_TYPE, PLACE_TYPE_COUNT);
					PLACE_TYPE.clear();
					PLACE_TYPE_COUNT.clear();
					break;
				}
				/** Place went in past 7 days **/
				case 7: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 7, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE", "Where TIME >=" + start + " and TIME<" + end, null);
					drawPie(PLACE, PLACE_COUNT);
					PLACE.clear();
					PLACE_COUNT.clear();
				}
				/** Time Spent past 30 days in place type **/
				case 8: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 30, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "Where TIME >=" + start + " and TIME<" + end, null);
					drawPie(PLACE_TYPE, PLACE_TYPE_COUNT);
					PLACE_TYPE.clear();
					PLACE_TYPE_COUNT.clear();
					break;
				}
				/** Place went in past 30 days **/
				case 9: {
					long start = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 30, 0, 0, 0);
					long end = TimeToUnix(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE", "Where TIME >=" + start + " and TIME<" + end, null);
					drawPie(PLACE, PLACE_COUNT);
					PLACE.clear();
					PLACE_COUNT.clear();
				}
				/** Time Spent in All in place type **/
				case 10: {
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE_TYPE", "", null);
					drawPie(PLACE_TYPE, PLACE_TYPE_COUNT);
					PLACE_TYPE.clear();
					PLACE_TYPE_COUNT.clear();
					break;
				}
				/** Place went in All **/
				case 11: {
					getfromDB(TabSQLite_SQLiteHelper.TABLE_NAME, "PLACE", "", null);
					drawPie(PLACE, PLACE_COUNT);
					PLACE.clear();
					PLACE_COUNT.clear();
				}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
	}

	/** draw the pie chart **/
	public void drawPie(ArrayList<String> string, ArrayList<Integer> Int) {
		Result_PieDetailsItem item;
		int maxCount = 0;
		int itemCount = 0;
		String itemslabel[] = string.toArray(new String[string.size()]);
		Integer items[] = Int.toArray(new Integer[Int.size()]);
		int total = 0;
		for (int i = 0; i < items.length; i++) {
			total = items[i] + total;
		}
		String percentage[] = new String[items.length];
		NF.setMaximumFractionDigits(2);
		for (int i = 0; i < items.length; i++) {
			percentage[i] = NF.format(((double) items[i] / total) * 100);
		}

		ArrayList<Integer> ColorArray = new ArrayList<Integer>();
		/** generate the color **/
		for (int i = 0; i < items.length; i++) {
			Random rand = new Random();
			int red = rand.nextInt(255);
			int green = rand.nextInt(255);
			int blue = rand.nextInt(255);
			int color = Color.rgb(red, green, blue);
			if (ColorArray.contains(color)) {
				break;
			} else {
				ColorArray.add(color);
			}
		}
		Integer[] colors = ColorArray.toArray(new Integer[ColorArray.size()]);
		PieListData = new ArrayList<HashMap<String, Object>>();
		/** generate the arcs **/
		for (int i = 0; i < items.length; i++) {
			itemCount = items[i];
			item = new Result_PieDetailsItem();
			item.count = itemCount;
			item.label = itemslabel[i];
			item.color = colors[i];
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("percentage", percentage[i] + "%");
			map.put("name", itemslabel[i]);
			PieListData.add(map);
			piedata.add(item);
			maxCount = maxCount + itemCount;

		}
		Piechart_listview();
		int size = 600;
		int BgColor = 0x000000;
		Bitmap mBaggroundImage = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
		Result_PieChartView piechart = new Result_PieChartView(this);
		piechart.setLayoutParams(new LayoutParams(size, size));
		piechart.setGeometry(size, size, 2, 2, 2, 2, R.drawable.piechart_back);
		piechart.setSkinParams(BgColor);
		piechart.setData(piedata, maxCount);
		piechart.invalidate();
		piechart.draw(new Canvas(mBaggroundImage));
		piechart = null;

		ImageView mImageView = new ImageView(this);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mImageView.setBackgroundColor(BgColor);
		mImageView.setImageBitmap(mBaggroundImage);
		LinearLayout finalLayout = (LinearLayout) findViewById(R.id.pie_container);
		finalLayout.removeAllViews();
		finalLayout.addView(mImageView);

	}

	/** List view **/
	public void Piechart_listview() {
		try {
			PieListAdapter = new SimpleAdapter(Result_Main.this, PieListData, R.layout.result_piechart_listview_item, new String[] { "percentage", "name" },
					new int[] { R.id.piechart_listview_percentage, R.id.piechart_listview_name }) {

			};

			PieList.setAdapter(PieListAdapter);
		} catch (Exception e) {
		}
	}

	/** get the specific date data from Database for pei chart view **/
	public void getfromDB(String table, String type, String Query1, String[] Query2) {
		String result;
		String query = "select " + type + " from " + table + " " + Query1;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, Query2);
		int which = -1;
		if (type.equals("PLACE")) {
			which = 0;
		} else if (type.equals("PLACE_TYPE")) {
			which = 1;
		}
		while (cursor.moveToNext()) {
			result = cursor.getString(cursor.getColumnIndex(type));

			switch (which) {
			case 0:
				if (PLACE.contains(result)) {
					int index = PLACE.indexOf(result);
					int count = PLACE_COUNT.get(index);
					count = count + 1;
					PLACE_COUNT.set(PLACE.indexOf(result), count);
				} else {
					PLACE.add(result);
					int count = 1;
					PLACE_COUNT.add(count);
				}
				break;
			case 1:
				if (PLACE_TYPE.contains(result)) {
					int index = PLACE_TYPE.indexOf(result);
					int count = PLACE_TYPE_COUNT.get(index);
					count = count + 1;
					PLACE_TYPE_COUNT.set(PLACE_TYPE.indexOf(result), count);
				} else {
					PLACE_TYPE.add(result);
					int count = 1;
					PLACE_TYPE_COUNT.add(count);
				}
				break;
			}
		}

	}

	/** menu function **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Pie Chart View").setIcon(R.drawable.menu_icon_piechart);
		menu.add(0, 1, 1, "Bar Chart View").setIcon(R.drawable.menu_icon_barchart);
		return super.onCreateOptionsMenu(menu);
	}

	/** on select listener of menu function **/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			piechartView();
			break;
		case 1:
			barchartView();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/** convert time to unix stamp **/
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

	@Override
	protected void onResume() {
		super.onResume();
	}
}
