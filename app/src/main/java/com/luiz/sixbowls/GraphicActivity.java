package com.luiz.sixbowls;

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

        PieDataSet pieDataSet = new PieDataSet(yEntries, "Expends by bowl");
        pieDataSet.setSliceSpace(1);
        pieDataSet.setValueTextSize(12);

        //add colors
        //pieDataSet.setColors();

        Legend legend = pieChartCurrent.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //Create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChartCurrent.setData(pieData);
        pieChartCurrent.invalidate();
    }
}
