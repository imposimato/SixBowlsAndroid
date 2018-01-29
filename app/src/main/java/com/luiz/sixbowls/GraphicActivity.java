package com.luiz.sixbowls;

// TODO: Rever fundo

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GraphicActivity extends AppCompatActivity {

    PieChart pieChartCurrent;
    BarChart barChartBalance;
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
        pieChartCurrent.setEntryLabelColor(Color.BLACK);
        pieChartCurrent.getDescription().setEnabled(false);

        addDataChartCurrent();

        barChartBalance = (BarChart) findViewById(R.id.graphBalance);
        barChartBalance.setTouchEnabled(true);
        barChartBalance.setDragEnabled(true);
        barChartBalance.setScaleEnabled(true);
        barChartBalance.getDescription().setEnabled(false);

        addDataChartBalance();

        pieChartCurrent.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // TODO: Report by bowl
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void addDataChartCurrent(){
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        String[] bowlsStr = getResources().getStringArray(R.array.bowls);
        for (String string : bowlsStr) {
            cursorTemp = db.rawQuery("SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND BOWL = '" + string +"'", new String[] {date1, date2});
            if (cursorTemp.moveToFirst()) {
                //String result = cursorTemp.getString(cursorTemp.getColumnIndex("TOTAL"));
                Float result = (float) cursorTemp.getDouble(0);
                if (result > 0) yEntries.add(new PieEntry(result, string));
            }
            cursorTemp.close();
        }

        PieDataSet pieDataSet = new PieDataSet(yEntries, "Expends by bowl");
        pieDataSet.setSliceSpace(1);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueFormatter(new MyValueFormatter());

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        Legend legend = pieChartCurrent.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //Create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChartCurrent.setData(pieData);
        pieChartCurrent.invalidate();
        pieChartCurrent.animateY(500);
    }

    private void addDataChartBalance(){
        ArrayList<BarEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();
        xEntries.add("Debit");
        xEntries.add("Credit");
        String sumCredQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND CREDDEB = 'C'";
        String sumDebQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND CREDDEB = 'D'";

        Cursor cursorSumCred = db.rawQuery(sumCredQuery, new String[]{date1, date2});
        Cursor cursorSumDeb = db.rawQuery(sumDebQuery, new String[]{date1, date2});

        if (cursorSumCred.moveToFirst()) {
            Float resultCred = (float) cursorSumCred.getDouble(0);
            yEntries.add(new BarEntry(0, resultCred));

        }
        if (cursorSumDeb.moveToFirst()) {
            Float resultDeb = (float) cursorSumDeb.getDouble(0);
            yEntries.add(new BarEntry(1, resultDeb));
        }

        if (cursorSumCred.moveToFirst() && cursorSumDeb.moveToFirst()){
            Float resultCred = (float) cursorSumCred.getDouble(0);
            Float resultDeb = (float) cursorSumDeb.getDouble(0);
            yEntries.add(new BarEntry(2, resultCred-resultDeb));
        }
        cursorSumCred.close();
        cursorSumDeb.close();

        BarDataSet dataSet = new BarDataSet(yEntries, "Income/Outcome/Balance");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setDrawValues(true);
        dataSet.setValueFormatter(new MyValueFormatter());

        BarData data = new BarData(dataSet);

        barChartBalance.setData(data);
        barChartBalance.setTouchEnabled(false);
        barChartBalance.invalidate();
        barChartBalance.animateY(500);

    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

}

class MyValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value);
    }
}
