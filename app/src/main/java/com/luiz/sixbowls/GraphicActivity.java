package com.luiz.sixbowls;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);

        pieChartCurrent = (PieChart) findViewById(R.id.graphCurrent);
        pieChartCurrent.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void loadDataChartCurrent(){
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();

        //TODO Populate the lists
        String[] bowlsStr = getResources().getStringArray(R.array.bowls);
        for (String string : bowlsStr) {
            String rawQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND";
            //Cursor cursor1 = db.rawQuery("SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND WHERE BOWL = '" + string + "'", new String[] {date1, date2});
            Cursor cursor1 = db.rawQuery("SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND WHERE BOWL = ?", new String[] {date1, date2, string.toUpperCase()});
            if (cursor1.moveToFirst()) {
                //note = cursor1.getString(cursor1.getColumnIndex("NOTE"));
                String result = cursor1.getString(cursor1.getColumnIndex(string.toUpperCase()));
                yEntries.add(new PieEntry(Float.parseFloat(result)));
                xEntries.add(string.toUpperCase());
            }
        }

        PieDataSet pieDataSet = new PieDataSet(yEntries, "Expends by bowl");
        pieDataSet.setSliceSpace(1);
        pieDataSet.setValueTextSize(12);

        //TODO add colors
        //pieDataSet.setColors();

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
