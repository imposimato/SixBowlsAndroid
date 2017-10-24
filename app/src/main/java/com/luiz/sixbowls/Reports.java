package com.luiz.sixbowls;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Reports extends AppCompatActivity implements DatePickerFragment.TheListener {

    Button dateInput1, dateInput2;
    TextView dateView1, dateView2;
    boolean aux1 = false;
    boolean aux2 = false;
    int date1, date2;
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        dateInput1 = (Button) findViewById(R.id.btnDateReport1);
        dateInput2 = (Button) findViewById(R.id.btnDateReport2);

        date1 = Integer.parseInt(String.valueOf(new StringBuilder().append(year).append(month+1).append(day)));
        date2 = Integer.parseInt(String.valueOf(new StringBuilder().append(year).append(month+1).append(day)));

        dateView1 = (TextView) findViewById(R.id.txtViewDateReport1);
        dateView2 = (TextView) findViewById(R.id.txtViewDateReport2);
        dateView1.setText(new StringBuilder().append(day).append("/")
                .append(month+1).append("/").append(year));
        dateView2.setText(new StringBuilder().append(day).append("/")
                .append(month+1).append("/").append(year));

        dbHelper = new SixBowlsDbHelper(this);
        db = dbHelper.getReadableDatabase();

        generateReport(date1, date2);

        dateInput1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
                aux1 = true;
            }
        });

        dateInput2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
                aux2 = true;
            }
        });
    }

    @Override
    public void returnDate(String date) {
        String resDate = date.substring(8,10) + "/" + date.substring(5,7) + "/" + date.substring(0,4);
        if (aux1){
            dateView1.setText(resDate);
            aux1 = false;
            try{
                date1 = Integer.parseInt( date.substring(0,4) + date.substring(5,7) + date.substring(8,10));
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (aux2){
            dateView2.setText(resDate);
            aux2 = false;
            try{
                date2 = Integer.parseInt( date.substring(0,4) + date.substring(5,7) + date.substring(8,10));
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        try {
            generateReport(date1, date2);
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void generateReport(int date1, int date2){
        if (date2 < date1){
            Toast.makeText(this, "Date In should be smaller or equal Date Out", Toast.LENGTH_SHORT).show();
        } else {
            EntriesFragment eFrag = new EntriesFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            eFrag.setDate1(date1);
            eFrag.setDate2(date2);
            ft.replace(R.id.fragCont,eFrag);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
