package com.mccc.bodyw;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private static final String CHART_VIEW_X = "view_x";
    private static final String CHART_HIGHLIGHT = "highlight";
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, ChartActivity.this);
        }
    };
    private class RecordData {
        private int index;
        private int date;
        private int weight;
        private int bodyFat;

        private RecordData(int index, int date, int weight, int bodyFat) {
            this.index = index;
            this.date = date;
            this.weight = weight;
            this.bodyFat = bodyFat;
        }
    }
    private class ChartValueSelectedListener implements OnChartValueSelectedListener {

        List<Integer> dataIndexList;
        TextView date = (TextView) findViewById(R.id.info_date);
        TextView leftValue = (TextView) findViewById(R.id.info_left_axis_value);
        TextView rightValue = (TextView) findViewById(R.id.info_right_axis_value);

        private ChartValueSelectedListener (List<Integer> dataIndexList) {
            this.dataIndexList = dataIndexList;
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (mRecordDataList == null) {
                return;
            }

            int index = (int) e.getX();
            if (dataIndexList != null && dataIndexList.contains(index)) {
                RecordData recordData = mRecordDataList.get(dataIndexList.indexOf(index));
                date.setText(String.valueOf(recordData.date));
                leftValue.setText(getString(R.string.value_weight_kg, valueFormat(recordData.weight)));
                rightValue.setText(getString(R.string.value_body_fat, valueFormat(recordData.bodyFat)));
            }
        }

        @Override
        public void onNothingSelected() {
            if (mRecordDataList == null) {
                return;
            }
            RecordData recordData = mRecordDataList.get(mRecordDataList.size() - 1);
            date.setText(
                    String.valueOf(recordData.date) + " " + getString(R.string.value_last_record));
            leftValue.setText(getString(R.string.value_weight_kg, valueFormat(recordData.weight)));
            rightValue.setText(getString(R.string.value_body_fat, valueFormat(recordData.bodyFat)));
        }
    }
    private Bundle mSavedInstance;
    private LineChart mLineChart;
    private List<RecordData> mRecordDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        mSavedInstance = savedInstanceState;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InputActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLineChart = (LineChart) findViewById(R.id.record_chart);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(
                AttributeContentProvider.RECORD_URI, true, mContentObserver);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, ChartActivity.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(
                        this, // Parent activity context
                        AttributeContentProvider.RECORD_URI, // Table to query
                        MainDatabaseHelper.getRecordQueryProjection(), // Projection to return
                        null, // No selection clause
                        null, // No selection arguments
                        MainDatabaseHelper.RecordEntry.COL_DATE  // Default sort order
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecordDataList = new ArrayList<>();
        refreshData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putFloat(CHART_VIEW_X, mLineChart.getLowestVisibleX());
        outState.putString(CHART_HIGHLIGHT, Arrays.toString(mLineChart.getHighlighted()));

        super.onSaveInstanceState(outState);
    }

    private void refreshData(Cursor cursor) {
        List<Integer> dataIndexList = getCursorData(cursor);
        mLineChart.setNoDataText("");
        if (mRecordDataList.size() == 0) {
            return;
        }

        ILineDataSet bodyWeightData = setBodyWeightData();
        ILineDataSet bodyFatData = setBodyFatData();
        LineData recordData = new LineData();
        recordData.addDataSet(bodyWeightData);
        recordData.addDataSet(bodyFatData);
        float leftMax = bodyWeightData.getYMax();
        float leftMin = bodyWeightData.getYMin();
        float rightMax = bodyFatData.getYMax();
        float rightMin = bodyFatData.getYMin();
        drawRecordChart(mLineChart, dataIndexList,
                mRecordDataList.get(mRecordDataList.size()-1).index + 1,
                leftMax, leftMin, rightMax, rightMin);
        mLineChart.setData(recordData);
        mLineChart.invalidate();
        mLineChart.setOnChartValueSelectedListener(new ChartValueSelectedListener(dataIndexList));

        TextView date = (TextView) findViewById(R.id.info_date);
        TextView leftValue = (TextView) findViewById(R.id.info_left_axis_value);
        TextView rightValue = (TextView) findViewById(R.id.info_right_axis_value);
        mLineChart.moveViewToX(mRecordDataList.get(mRecordDataList.size()-1).index + 1);
        if (mSavedInstance != null) {
            mLineChart.moveViewToX(mSavedInstance.getFloat(CHART_VIEW_X));
            String highlightString = mSavedInstance.getString(CHART_HIGHLIGHT);
            Highlight highlight = parseHighlight(highlightString);
            mLineChart.highlightValue(highlight);
            if (highlight != null) {
                int index = (int) highlight.getX();
                if (dataIndexList != null && dataIndexList.contains(index)) {
                    RecordData highlightData = mRecordDataList.get(dataIndexList.indexOf(index));
                    date.setText(String.valueOf(highlightData.date));
                    leftValue.setText(
                            getString(R.string.value_weight_kg, valueFormat(highlightData.weight)));
                    rightValue.setText(
                            getString(R.string.value_body_fat, valueFormat(highlightData.bodyFat)));
                }
                return;
            }
        }
        RecordData lastData = mRecordDataList.get(mRecordDataList.size() - 1);
        date.setText(
                String.valueOf(lastData.date) + " " + getString(R.string.value_last_record));
        leftValue.setText(getString(R.string.value_weight_kg, valueFormat(lastData.weight)));
        rightValue.setText(getString(R.string.value_body_fat, valueFormat(lastData.bodyFat)));
    }

    private List<Integer> getCursorData(Cursor cursor) {
        List<Integer> indexDataList = new ArrayList<>();
        int index = 0;
        int count = 0;
        while (cursor != null && cursor.moveToNext()) {
            int tempIndex;
            if (count > 0) {
                tempIndex = calIndexByDate(index, count,
                        cursor.getInt(MainDatabaseHelper.RecordQuery.DATE));
                mRecordDataList.add(new RecordData(
                        tempIndex,
                        cursor.getInt(MainDatabaseHelper.RecordQuery.DATE),
                        cursor.getInt(MainDatabaseHelper.RecordQuery.WEIGHT),
                        cursor.getInt(MainDatabaseHelper.RecordQuery.BODY_FAT)));
                index = tempIndex;
            } else {
                mRecordDataList.add(new RecordData(
                        0,
                        cursor.getInt(MainDatabaseHelper.RecordQuery.DATE),
                        cursor.getInt(MainDatabaseHelper.RecordQuery.WEIGHT),
                        cursor.getInt(MainDatabaseHelper.RecordQuery.BODY_FAT)));
            }
            count = count + 1;
            indexDataList.add(index);
        }
        return indexDataList;
    }

    private int calIndexByDate(int lastIndex, int count, int nowDate) {
        int lastDate = mRecordDataList.get(count-1).date;
        int lastYear = lastDate / 10000;
        int lastMonth = (lastDate % 10000) / 100;
        int lastDay = lastDate % 100;

        int nowYear = nowDate / 10000;
        int nowMonth = (nowDate % 10000) / 100;
        int nowDay = nowDate % 100;

        if (lastYear == nowYear && lastMonth == nowMonth) {
            return lastIndex + (nowDay - lastDay);
        } else if (lastYear == nowYear) {
            for (int i = 1; i < nowMonth - lastMonth + 1; i++) {
                int tempMonth = nowMonth - i;
                nowDay = nowDay + daysOfMonth(lastYear, tempMonth);
            }
            return lastIndex + (nowDay - lastDay);
        } else {
            int gapDays = 0;
            for (int i = 1; i < nowYear - lastYear + 1; i++) {
                int tempYear = nowYear - i;
                if (tempYear % 400 == 0 || (tempYear % 4 == 0 && tempYear % 100 != 0)) {
                    gapDays = gapDays + 366;
                } else {
                    gapDays = gapDays + 365;
                }
                if (i == nowYear - lastYear) {
                    for (int j = 1; j < lastMonth + 1; j++) {
                        gapDays = gapDays - daysOfMonth(lastYear, j);
                    }
                    gapDays = gapDays + lastDay;
                    for (int j = 1; j < nowMonth; j++) {
                        gapDays = gapDays + daysOfMonth(nowYear, j);
                    }
                    gapDays = gapDays + nowDay;
                }
            }
            return lastIndex + gapDays;
        }
    }

    private int daysOfMonth(int year, int month) {
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            return 31;
        } else if (month == 2) {
            if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return  30;
        }
    }

    private static final float DATA_SET_LINE_WIDTH = 3;
    private static final float DATA_SET_CIRCLE_RADIUS = 5;
    private static final int DATA_SET_LEFT_AXIS_COLOR = Color.CYAN;
    private static final int DATA_SET_RIGHT_AXIS_COLOR = Color.YELLOW;
    private static final float CHART_VIEW_POINT_NUM = 7;
    private static final float CHART_BORDER_WIDTH = 2;
    private static final float CHART_AXIS_LABEL_WIDTH = 40;
    private static final int CHART_BORDER_COLOR = Color.LTGRAY;
    private static final float CHART_ROTATION_ANGLE = -40;
    private static final float CHART_AXIS_OFFSET = 5;

    private ILineDataSet setBodyWeightData() {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < mRecordDataList.size(); i++) {
            if (mRecordDataList.get(i).weight == 0) {
                continue;
            }
            entries.add(new Entry(
                    mRecordDataList.get(i).index,
                    mRecordDataList.get(i).weight,
                    DATA_SET_LEFT_AXIS_COLOR));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Body Weight");
        dataSet.setColor(DATA_SET_LEFT_AXIS_COLOR);
        dataSet.setLineWidth(DATA_SET_LINE_WIDTH);
        dataSet.setCircleColor(DATA_SET_LEFT_AXIS_COLOR);
        dataSet.setCircleRadius(DATA_SET_CIRCLE_RADIUS);
        dataSet.setCircleColorHole(DATA_SET_LEFT_AXIS_COLOR);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawValues(false);
        dataSet.setHighLightColor(DATA_SET_LEFT_AXIS_COLOR);
        dataSet.setDrawVerticalHighlightIndicator(false);

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        TextView leftAxis = (TextView) findViewById(R.id.left_axis);
        leftAxis.setText(getString(R.string.label_weight));
        TextView leftTitle = (TextView) findViewById(R.id.info_left_axis_title);
        leftTitle.setText(getString(R.string.title_weight).concat(" = "));

        return dataSet;
    }

    private ILineDataSet setBodyFatData() {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < mRecordDataList.size(); i++) {
            if (mRecordDataList.get(i).bodyFat == 0) {
                continue;
            }
            entries.add(new Entry(
                    mRecordDataList.get(i).index,
                    mRecordDataList.get(i).bodyFat,
                    DATA_SET_RIGHT_AXIS_COLOR));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Body Fat");
        dataSet.setColor(DATA_SET_RIGHT_AXIS_COLOR);
        dataSet.setLineWidth(DATA_SET_LINE_WIDTH);
        dataSet.setCircleColor(DATA_SET_RIGHT_AXIS_COLOR);
        dataSet.setCircleRadius(DATA_SET_CIRCLE_RADIUS);
        dataSet.setCircleColorHole(DATA_SET_RIGHT_AXIS_COLOR);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawValues(false);
        dataSet.setHighLightColor(DATA_SET_RIGHT_AXIS_COLOR);
        dataSet.setDrawVerticalHighlightIndicator(false);

        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        TextView rightAxis = (TextView) findViewById(R.id.right_axis);
        rightAxis.setText(getString(R.string.label_body_fat));
        TextView rightTitle = (TextView) findViewById(R.id.info_right_axis_title);
        rightTitle.setText(getString(R.string.title_body_fat).concat(" = "));

        return dataSet;
    }

    private void drawRecordChart(LineChart chart, final List<Integer> dataIndexList, final int size,
                                 float leftMax, float leftMin, float rightMax, float rightMin) {
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDrawBorders(true);
        chart.setBorderColor(CHART_BORDER_COLOR);
        chart.setBorderWidth(CHART_BORDER_WIDTH);
        chart.setScaleMinima(size / (CHART_VIEW_POINT_NUM + 0.25f), 1);

        Legend legend = chart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextColor(CHART_BORDER_COLOR);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setXEntrySpace(30);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setMinWidth(CHART_AXIS_LABEL_WIDTH);
        rightAxis.setMaxWidth(CHART_AXIS_LABEL_WIDTH);
        rightAxis.setTextColor(CHART_BORDER_COLOR);
        rightAxis.setAxisLineWidth(1);
        rightAxis.setAxisLineColor(DATA_SET_RIGHT_AXIS_COLOR);
        rightAxis.setAxisMaximum(rightMax + CHART_AXIS_OFFSET);
        rightAxis.setAxisMinimum(rightMin - CHART_AXIS_OFFSET);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setMinWidth(CHART_AXIS_LABEL_WIDTH);
        leftAxis.setMaxWidth(CHART_AXIS_LABEL_WIDTH);
        leftAxis.setTextColor(CHART_BORDER_COLOR);
        leftAxis.setAxisLineWidth(1);
        leftAxis.setAxisLineColor(DATA_SET_LEFT_AXIS_COLOR);
        leftAxis.setAxisMaximum(leftMax + CHART_AXIS_OFFSET);
        leftAxis.setAxisMinimum(leftMin - CHART_AXIS_OFFSET);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(size - CHART_VIEW_POINT_NUM >= 0? -1 : size - CHART_VIEW_POINT_NUM - 1);
        xAxis.setAxisMaximum(size);
        xAxis.setTextColor(CHART_BORDER_COLOR);
        xAxis.setLabelRotationAngle(CHART_ROTATION_ANGLE);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                if (index < 0 || index > size - 1 || !dataIndexList.contains(index)) {
                    return "";
                }
                return String.valueOf(mRecordDataList.get(dataIndexList.indexOf(index)).date)
                        .substring(4);
            }
        });

        XYMarkerView markerView = new XYMarkerView(this);
        markerView.setChartView(chart); // For bounds control
        chart.setMarker(markerView);
    }

    private String valueFormat(float value) {
        return new DecimalFormat("###.##").format(value);
    }

    private Highlight parseHighlight(String string) {
        Highlight highlight = null;
        String[] array = string.split(",");
        if (array.length == 5) {
            float x = Float.valueOf(array[1].split(": ")[1]);
            float y = Float.valueOf(array[2].split(": ")[1]);
            int index = Integer.valueOf(array[3].split(": ")[1]);
            highlight = new Highlight(x, y, index);
        }
        return highlight;
    }

}
