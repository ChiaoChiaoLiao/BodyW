package com.mccc.bodyw;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import java.util.Map;

import static com.mccc.bodyw.AttributeContentProvider.CONTENT_URI;
import static com.mccc.bodyw.MainDatabaseHelper.COL_DATE;

public class ChartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        LineChart lineChart = (LineChart) findViewById(R.id.record_chart);
        ILineDataSet bodyWeightData = setBodyWeightData();
        ILineDataSet bodyFatData = setBodyFatData();
        LineData recordData = new LineData();
        recordData.addDataSet(bodyWeightData);
        recordData.addDataSet(bodyFatData);
        drawRecordChart(lineChart, 30);
        lineChart.setData(recordData);
        lineChart.invalidate();


        ContentValues values = new ContentValues();
        for (int i = 0; i < 10; i++) {
            values.put(COL_DATE, i);
            getContentResolver().insert(CONTENT_URI, values);
        }

        try (Cursor cursor = getContentResolver().query(CONTENT_URI, null, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext())
                    Log.d("mingchun", "date = " + cursor.getInt(cursor.getColumnIndex(COL_DATE)));
            }
        }

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

    private ILineDataSet setBodyWeightData() {
        List<Entry> entries = new ArrayList<>();
        Map<Integer, Integer> weightMap = AttributeContentProvider.getWeight();

        for (int index = 0; index < weightMap.size(); index++)
            entries.add(new Entry(index, (int) (Math.random() * 10) + 60));

        LineDataSet dataSet = new LineDataSet(entries, "Body Weight");
        dataSet.setColor(Color.RED);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleColorHole(Color.RED);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawValues(false);
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setHighLightColor(Color.LTGRAY);

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return dataSet;
    }

    private ILineDataSet setBodyFatData() {
        List<Entry> entries = new ArrayList<>();
        Map<Integer, Integer> bodyFatMap = AttributeContentProvider.getBodyFat();

        for (int index = 0; index < bodyFatMap.size(); index++)
            entries.add(new Entry(index, (int) (Math.random() * 10) + 20));

        LineDataSet dataSet = new LineDataSet(entries, "Body Fat");
        dataSet.setColor(Color.GREEN);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.GREEN);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleColorHole(Color.GREEN);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawValues(false);
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setHighLightColor(Color.LTGRAY);

        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        return dataSet;
    }

    private void drawRecordChart(LineChart chart, int size) {
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDrawBorders(true);
        chart.setScaleMinima(size/7.25f, 1);

        Legend legend = chart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setMinWidth(30);
        rightAxis.setMaxWidth(30);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setMinWidth(30);
        leftAxis.setMaxWidth(30);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });

        chart.moveViewToX(size);
    }
}
