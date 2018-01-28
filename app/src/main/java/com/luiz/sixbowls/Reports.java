package com.luiz.sixbowls;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.luiz.sixbowls.MainActivity.parseDate;

public class Reports extends AppCompatActivity implements DatePickerFragment.TheListener {

    Button dateInput1, dateInput2, graphicBt;
    TextView dateView1, dateView2;
    static TextView resultCredTV, resultDebTV, balanceTV;
    boolean aux1 = false;
    boolean aux2 = false;

    Date date1, date2 = new Date();
    Calendar calendar;
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dtShow = new SimpleDateFormat("dd/MM/yyyy");
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    BalanceUpdate balanceUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        //Set to the first day of the month
        calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        date1 = calendar.getTime();

        //Starting the buttons
        dateInput1 = (Button) findViewById(R.id.btnDateReport1);
        dateInput2 = (Button) findViewById(R.id.btnDateReport2);
        graphicBt = (Button) findViewById(R.id.graphicBt);

        //Starting the Textviews and setting the values
        dateView1 = (TextView) findViewById(R.id.txtViewDateReport1);
        dateView2 = (TextView) findViewById(R.id.txtViewDateReport2);
        dateView1.setText(dtShow.format(date1));
        dateView2.setText(dtShow.format(date2));

        resultCredTV = (TextView) findViewById(R.id.resultCredTV);
        resultDebTV = (TextView) findViewById(R.id.resultDebTV);
        balanceTV = (TextView) findViewById(R.id.balanceTV);

        dbHelper = new SixBowlsDbHelper(this);
        db = dbHelper.getReadableDatabase();
        balanceUpdate = new BalanceUpdate();

        generateReport(dt.format(date1), dt.format(date2));

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

        graphicBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePie(dt.format(date1), dt.format(date2));
            }
        });
    }

    @Override
    public void returnDate(String date) {
        Date currentDate = parseDate(date);
        String resDate = dtShow.format(currentDate);

        if (aux1){
            dateView1.setText(resDate);
            aux1 = false;
            try{
                date1 = currentDate;
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (aux2){
            dateView2.setText(resDate);
            aux2 = false;
            try{
                date2 = currentDate;
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        try {
            generateReport(dt.format(date1), dt.format(date2));
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void generateReport(String dateStr1, String dateStr2){
        date1 = parseDate(dateStr1);
        date2 = parseDate(dateStr2);
        if (date1.after(date2)){
            Toast.makeText(this, "Date In should be smaller or equal Date Out", Toast.LENGTH_SHORT).show();
        } else {
            EntriesFragment eFrag = new EntriesFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            eFrag.setDate1(dt.format(date1));
            eFrag.setDate2(dt.format(date2));
            ft.replace(R.id.fragCont,eFrag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            balanceUpdate.updateBalance(dt.format(date1), dt.format(date2));
        }
    }

    public void generatePie(String dateStr1, String dateStr2){
        Intent intent = new Intent(this, GraphicActivity.class);
        intent.putExtra("date1", dateStr1);
        intent.putExtra("date2", dateStr2);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menureports, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuDelete:
                Toast.makeText(this, "Longpress and Item in the list to delete it!", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
