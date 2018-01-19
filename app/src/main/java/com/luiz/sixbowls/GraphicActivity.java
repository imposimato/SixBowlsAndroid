package com.luiz.sixbowls;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class GraphicActivity extends AppCompatActivity {

    PieChart pieChartCurrent, pieChartBalance;
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;
    private String date1, date2;
    Cursor cursorTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);

        dbHelper = new SixBowlsDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        date1 = intent.getStringExtra("date1");
        date2 = intent.getStringExtra("date2");

        pieChartCurrent = (PieChart) findViewById(R.id.graphCurrent);
        pieChartCurrent.setRotationEnabled(true);
        pieChartCurrent.setHoleRadius(25f);
        pieChartCurrent.setTransparentCircleAlpha(0);
        pieChartCurrent.setCenterText("Expenses");
        pieChartCurrent.setCenterTextSize(10);
        //pieChartCurrent.setDrawEntryLabels(true);

        addDataChartCurrent();


        pieChartCurrent.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void addDataChartCurrent(){
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();
        //TODO Populate the lists
        String[] bowlsStr = getResources().getStringArray(R.array.bowls);
        for (String string : bowlsStr) {
            cursorTemp = db.rawQuery("SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND BOWL = '" + string +"'", new String[] {date1, date2});
            if (cursorTemp.moveToFirst()) {
                //String result = cursorTemp.getString(cursorTemp.getColumnIndex("TOTAL"));
                Float result = (float) cursorTemp.getDouble( 0);
                yEntries.add(new PieEntry(result, string));
            }
            cursorTemp.close();
        }

        PieDataSet pieDataSet = new PieDataSet(yEntries, "Expends by bowl");
        pieDataSet.setSliceSpace(1);
        pieDataSet.setValueTextSize(12);

        //TODO add colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.CYAN);
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);


        Legend legend = pieChartCurrent.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //Create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChartCurrent.setData(pieData);
        pieChartCurrent.invalidate();
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

}
