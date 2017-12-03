package com.luiz.sixbowls;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.luiz.sixbowls.MainActivity.parseDate;

public class Reports extends AppCompatActivity implements DatePickerFragment.TheListener {

    Button dateInput1, dateInput2;
    TextView dateView1, dateView2;
    static TextView resultCredTV, resultDebTV, balanceTV;
    boolean aux1 = false;
    boolean aux2 = false;

    double totalCred = 0;
    double totalDeb = 0;

    Date date1 = new Date(), date2 = new Date();
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dtShow = new SimpleDateFormat("dd/MM/yyyy");
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        dateInput1 = (Button) findViewById(R.id.btnDateReport1);
        dateInput2 = (Button) findViewById(R.id.btnDateReport2);

        dateView1 = (TextView) findViewById(R.id.txtViewDateReport1);
        dateView2 = (TextView) findViewById(R.id.txtViewDateReport2);
        dateView1.setText(dtShow.format(date1));
        dateView2.setText(dtShow.format(date2));

        resultCredTV = (TextView) findViewById(R.id.resultCredTV);
        resultDebTV = (TextView) findViewById(R.id.resultDebTV);
        balanceTV = (TextView) findViewById(R.id.balanceTV);

        dbHelper = new SixBowlsDbHelper(this);
        db = dbHelper.getReadableDatabase();

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

            updateBalance();
        }
    }

    public void updateBalance() {

        String sumCredQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND CREDDEB = 'C'";
        String sumDebQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND CREDDEB = 'D'";

        Cursor cursorSumCred = db.rawQuery(sumCredQuery, new String[] {dt.format(date1), dt.format(date2)});
        Cursor cursorSumDeb = db.rawQuery(sumDebQuery, new String[] {dt.format(date1), dt.format(date2)});

        if (cursorSumCred.moveToFirst()){
            totalCred = cursorSumCred.getDouble(cursorSumCred.getColumnIndex("TOTAL"));
            resultCredTV.setText("Total Credit: " + String.format("%.2f", totalCred));
        }

        if (cursorSumDeb.moveToFirst()){
            totalDeb = cursorSumDeb.getDouble(cursorSumCred.getColumnIndex("TOTAL"));
            resultDebTV.setText("Total Debit: " + String.format("%.2f", totalDeb));
        }

        if (totalCred - totalDeb < 0){
            balanceTV.setTextColor(Color.RED);
        } else {
            balanceTV.setTextColor(Color.GREEN);
        }

        balanceTV.setText("Balance : " + String.format("%.2f", (totalCred - totalDeb)));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
