package com.luiz.sixbowls;

// TODO: LAYOUT
// TODO: Rodar BD backgroud
// TODO: Swipe Views
// TODO: OCR Button
// TODO: Export/Import CSV file

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.TheListener {

    Button dateInput , insertEntryBt;
    RadioButton credRB, debRB;
    TextView dateView;
    EditText moneyInput;
    Spinner spinner;
    RadioGroup radioGroup;

    SimpleDateFormat dtShow = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

    Date currentDate = new Date();
    String dateInputDB = dt.format(currentDate);

    int spinnerItem = 0;
    double entry;
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    ArrayList<String> bowls;
    ArrayAdapter arrayAdapter;

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
        moneyInput = (EditText) findViewById(R.id.moneyInput);
        credRB = (RadioButton) findViewById(R.id.credRB);
        debRB = (RadioButton) findViewById(R.id.debRB);
        dbHelper = new SixBowlsDbHelper(this);
        db = dbHelper.getWritableDatabase();
        String[] bowlsStr = getResources().getStringArray(R.array.bowls);
        bowls = new ArrayList<String>();
        for (String string : bowlsStr){
            bowls.add(string);
        }

        spinner = (Spinner) findViewById(R.id.bowl);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bowls);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItem = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateInput.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        moneyInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    insertEntry(v);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.credDebRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.credRB){
                    spinner.setEnabled(false);
                }
                if (checkedId == R.id.debRB){
                    spinner.setEnabled(true);
                }
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

            if (credRB.isChecked())  {
                entryValue.put("CREDDEB", "C");
                entryValue.put("BOWL", "CREDIT");
            }
            if (debRB.isChecked()) {
                entryValue.put("CREDDEB", "D");
                entryValue.put("BOWL", bowls.get(spinnerItem));
            }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuAbout:
                new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                        .setTitle("Six Bowls")
                        .setMessage(R.string.aboutDescription)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setNeutralButton("Ok", null)
                        .create()
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}