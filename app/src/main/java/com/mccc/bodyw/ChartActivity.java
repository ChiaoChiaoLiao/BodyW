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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, ChartActivity.this);
        }
    };
    private class RecordData {
        private int date;
        private int weight;
        private int bodyFat;

        private RecordData(int date, int weight, int bodyFat) {
            this.date = date;
            this.weight = weight;
            this.bodyFat = bodyFat;
        }
    }
    private LineChart mLineChart;
    private List<RecordData> mRecordDataList;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
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

    private void refreshData(Cursor cursor) {
        mRecordDataList = getCursorData(cursor);
        mLineChart.setNoDataText("");
        if (mRecordDataList.size() == 0) {
            mFab.callOnClick();
            return;
        }
        ILineDataSet bodyWeightData = setBodyWeightData();
        ILineDataSet bodyFatData = setBodyFatData();
        LineData recordData = new LineData();
        recordData.addDataSet(bodyWeightData);
        recordData.addDataSet(bodyFatData);
        drawRecordChart(mLineChart, cursor.getCount());
        mLineChart.setData(recordData);
        mLineChart.invalidate();
    }

    private List<RecordData> getCursorData(Cursor cursor) {
        List<RecordData> recordDataList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            recordDataList.add(new RecordData(cursor.getInt(MainDatabaseHelper.RecordQuery.DATE),
                    cursor.getInt(MainDatabaseHelper.RecordQuery.WEIGHT),
                    cursor.getInt(MainDatabaseHelper.RecordQuery.BODY_FAT)));
        }
        return recordDataList;
    }

    private ILineDataSet setBodyWeightData() {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < mRecordDataList.size(); i++) {
            entries.add(new Entry(i, mRecordDataList.get(i).weight));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Body Weight");
        dataSet.setColor(Color.CYAN);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.CYAN);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleColorHole(Color.CYAN);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawValues(false);
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setHighLightColor(Color.CYAN);

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        TextView leftAxis = (TextView) findViewById(R.id.left_axis);
        leftAxis.setText(getString(R.string.label_weight));

        return dataSet;
    }

    private ILineDataSet setBodyFatData() {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < mRecordDataList.size(); i++) {
            entries.add(new Entry(i, mRecordDataList.get(i).bodyFat));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Body Fat");
        dataSet.setColor(Color.YELLOW);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.YELLOW);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleColorHole(Color.YELLOW);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawValues(false);
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setHighLightColor(Color.YELLOW);

        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        TextView rightAxis = (TextView) findViewById(R.id.right_axis);
        rightAxis.setText(getString(R.string.label_body_fat));

        return dataSet;
    }

    private void drawRecordChart(LineChart chart, int size) {
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.LTGRAY);
        chart.setBorderWidth(2);
        chart.setScaleMinima(size/7.25f, 1);

        Legend legend = chart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextColor(Color.LTGRAY);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setXEntrySpace(30);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setMinWidth(30);
        rightAxis.setMaxWidth(30);
        rightAxis.setTextColor(Color.LTGRAY);
        rightAxis.setAxisLineWidth(1);
        rightAxis.setAxisLineColor(Color.YELLOW);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setMinWidth(30);
        leftAxis.setMaxWidth(30);
        leftAxis.setTextColor(Color.LTGRAY);
        leftAxis.setAxisLineWidth(1);
        leftAxis.setAxisLineColor(Color.CYAN);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(mRecordDataList.size()-7 >= 0? 0 : mRecordDataList.size()-7);
        xAxis.setAxisMaximum(mRecordDataList.size()-1);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setLabelRotationAngle(-40);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if ((int) value < 0 || (int) value > mRecordDataList.size()) {
                    return "";
                }
                return String.valueOf(mRecordDataList.get((int) value).date).substring(4);
            }
        });

        chart.moveViewToX(size);
    }

}
