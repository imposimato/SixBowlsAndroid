package com.luiz.sixbowls;

// TODO: Strings in the Resources
// TODO: Menu
// TODO: Export/Import CSV file (Upload to Drive)

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
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

import com.google.android.gms.common.api.CommonStatusCodes;
import com.luiz.sixbowls.ocr.OcrCaptureActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements DatePickerFragment.TheListener {

    Button dateInput , insertEntryBt, ocrStart;
    RadioButton credRB, debRB;
    TextView dateView;
    EditText moneyInput, obsText;
    Spinner spinner;
    RadioGroup radioGroup;

    SimpleDateFormat dtShow = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

    Date currentDate = new Date();
    String dateInputDB = dt.format(currentDate);

    int spinnerItem = 0;
    double entry;
    String observation;
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    private static final int RC_OCR_CAPTURE = 9003;

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

        //ocrStart = (Button) findViewById(R.id.ocrStart);

        radioGroup = (RadioGroup) findViewById(R.id.credDebRadioGroup);
        credRB = (RadioButton) findViewById(R.id.credRB);
        debRB = (RadioButton) findViewById(R.id.debRB);

        obsText = (EditText) findViewById(R.id.obsText);

        String[] bowlsStr = getResources().getStringArray(R.array.bowls);
        bowls = new ArrayList<>();
        for (String string : bowlsStr){
            bowls.add(string);
        }

        spinner = (Spinner) findViewById(R.id.bowl);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bowls);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
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

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        };

        dateView.setOnClickListener(onClickListener);
        dateInput.setOnClickListener(onClickListener);

        View.OnKeyListener enterListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    insertEntry(v);

                }
                return false;
            }
        };

        obsText.setOnKeyListener(enterListener);

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

    public void ocrCapture(View view){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AutoFocus, true);
        intent.putExtra(OcrCaptureActivity.UseFlash, false);

        startActivityForResult(intent, RC_OCR_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    moneyInput.setText(String.valueOf(text));
                } else {
                    Toast.makeText(MainActivity.this, "It was not Possible to Read", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, CommonStatusCodes.getStatusCodeString(resultCode), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void insertEntry(View view){
        try {
            new InsertEntry().execute();

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Toast.makeText(MainActivity.this, "Entry Registered!", Toast.LENGTH_SHORT).show();

            moneyInput.setText(null);
            obsText.setText(null);

        } catch (Exception e) {
            Toast.makeText(this, "Something wrong is not right!", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToReportsAct(View view){
        try {
            startActivity(new Intent(this, Reports.class));
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
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
            case R.id.reports_menu:
                startActivity(new Intent(this, Reports.class));
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class InsertEntry extends AsyncTask<Integer, Void, Boolean> {

        ContentValues entryValue;

        protected void onPreExecute() {

            entry = Double.parseDouble(moneyInput.getText().toString());
            observation = obsText.getText().toString();
            entryValue = new ContentValues();

            entryValue.put("DATE", dateInputDB);
            entryValue.put("ENTRY", entry);
            entryValue.put("NOTE", observation);

            if (credRB.isChecked())  {
                entryValue.put("CREDDEB", "C");
                entryValue.put("BOWL", "CREDIT");
            }

            if (debRB.isChecked()) {
                entryValue.put("CREDDEB", "D");
                entryValue.put("BOWL", bowls.get(spinnerItem));
            }
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                dbHelper = new SixBowlsDbHelper(getApplicationContext());
                db = dbHelper.getWritableDatabase();
                db.insert("INOUT", null, entryValue);
                db.close();
            } catch (SQLException e){
                return false;
            }
            return null;
        }
    }
}