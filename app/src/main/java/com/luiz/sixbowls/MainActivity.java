package com.luiz.sixbowls;

// TODO: 20/11/2017  LAYOUT
// TODO: 20/11/2017  Rodar BD backgroud

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.TheListener {

    Button dateInput , insertEntryBt;
    RadioButton credRB, debRB;
    TextView dateView, moneyInput;
    SimpleDateFormat dtShow = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    Date currentDate = new Date();
    String dateInputDB = dt.format(currentDate);
    double entry;
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    public MainActivity() throws ParseException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateInput = (Button) findViewById(R.id.dateInput);
        insertEntryBt = (Button) findViewById(R.id.insertEntryBt);
        dateView = (TextView) findViewById(R.id.dateTextView);
        dateView.setText(dtShow.format(currentDate));
        moneyInput = (TextView) findViewById(R.id.moneyInput);
        credRB = (RadioButton) findViewById(R.id.credRB);
        debRB = (RadioButton) findViewById(R.id.debRB);
        dbHelper = new SixBowlsDbHelper(this);
        db = dbHelper.getWritableDatabase();

        dateInput.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });
    }


    @Override
    public void returnDate(String date) {
        dateInputDB = date;
        this.currentDate = parseDate(date);
        String resDate = dtShow.format(this.currentDate);
        dateView.setText(resDate);
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

    public static Date parseDate(String date){
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
    //TODO Bowls
    //TODO Menu superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuAbout:
                Toast.makeText(this, "About the App", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}