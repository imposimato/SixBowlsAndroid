package com.luiz.sixbowls;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.TheListener{

    Button dateInput , insertEntryBt;
    RadioButton credRB, debRB;
    TextView dateView, moneyInput;
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    int dateInputDB;
    double entry;
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateInput = (Button) findViewById(R.id.dateInput);
        insertEntryBt = (Button) findViewById(R.id.insertEntryBt);
        dateView = (TextView) findViewById(R.id.dateTextView);
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month+1).append("/").append(year));
        dateInputDB = Integer.parseInt(String.valueOf(new StringBuilder().append(year).append(month+1).append(day)));
        moneyInput = (TextView) findViewById(R.id.moneyInput);
        credRB = (RadioButton) findViewById(R.id.credRB);
        debRB = (RadioButton) findViewById(R.id.debRB);
        dbHelper = new SixBowlsDbHelper(this);
        db = dbHelper.getWritableDatabase();



        dateInput.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public void returnDate(String date) {
        String resDate = date.substring(8,10) + "/" + date.substring(5,7) + "/" + date.substring(0,4);
        dateView.setText(resDate);
        try{
            dateInputDB = Integer.parseInt( date.substring(0,4) + date.substring(5,7) + date.substring(8,10));
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void insertEntry(View view){
        try{
            entry = Double.parseDouble(moneyInput.getText().toString());
            ContentValues entryValue = new ContentValues();
            entryValue.put("ENTRY", entry);
            entryValue.put("DATE", dateInputDB);
            if (credRB.isChecked()) entryValue.put("CREDDEB", "C");
            if (debRB.isChecked()) entryValue.put("CREDDEB", "D");
            db.insert("INOUT", null, entryValue);
            moneyInput.setText(null);
        } catch (Exception e) {
            Toast.makeText(this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
        }

    }

    public void goToReportsAct(View view){
        try {
            Intent it = new Intent(this, Reports.class);
            startActivity(it);
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
}