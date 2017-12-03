package com.luiz.sixbowls;

import android.app.Dialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.zip.Inflater;

public class EntriesFragment extends ListFragment {

    // Attributes
    private Context mContext;
    private Cursor cursor;
    private String date1, date2;
    double totalCred = 0;
    double totalDeb = 0;
    String whereClause = "DATE BETWEEN ? AND ?";
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    CursorAdapter listAdapter;

    // Elements
    private ListView listEntries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our context
        mContext = getActivity();

        // Let's inflate & return the view
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // Get the database handler & the cursor
        try{
            dbHelper = new SixBowlsDbHelper(mContext);
            db = dbHelper.getWritableDatabase();

            listEntries = getListView();

            updateCursor();

            listEntries.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, final long id) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Entry")
                            .setMessage("Do you really want to delete this entry?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String string = String.valueOf(id);
                                    db.execSQL("DELETE FROM INOUT WHERE _id = '" + string + "'");
                                    updateCursor();
                                    updateBalance();

                                }})
                            .setNegativeButton(android.R.string.no, null)
                            .create().show();

                    return true;
                }
            });

        } catch (Exception e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void updateBalance() {

        String sumCredQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND CREDDEB = 'C'";
        String sumDebQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND CREDDEB = 'D'";

        Cursor cursorSumCred = db.rawQuery(sumCredQuery, new String[] {date1, date2});
        Cursor cursorSumDeb = db.rawQuery(sumDebQuery, new String[] {date1, date2});

        if (cursorSumCred.moveToFirst()){
            totalCred = cursorSumCred.getInt(cursorSumCred.getColumnIndex("TOTAL"));
            Reports.resultCredTV.setText("Total Credit: " + String.valueOf(totalCred));
        }

        if (cursorSumDeb.moveToFirst()){
            totalDeb = cursorSumDeb.getInt(cursorSumCred.getColumnIndex("TOTAL"));
            Reports.resultDebTV.setText("Total Debit: " + String.valueOf(totalDeb));
        }

        if (totalCred - totalDeb < 0){
            Reports.balanceTV.setTextColor(Color.RED);
        } else {
            Reports.balanceTV.setTextColor(Color.GREEN);
        }

        Reports.balanceTV.setText("Balance : " + String.valueOf(totalCred - totalDeb));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    public void updateCursor(){
        cursor = db.query("INOUT",
                new String[]{"_id", "ENTRY", "printf('%.2f', ENTRY) as ENTRYF",
                        "DATE", "strftime('%d/%m/%Y', DATE) as DATEF", "CREDDEB"},
                whereClause, new String[] {date1, date2},
                null, null, "DATE");
        CursorAdapter listAdapter2 = new android.widget.SimpleCursorAdapter(mContext,
                R.layout.fragment_item,
                cursor,
                new String[]{"ENTRYF", "DATEF", "CREDDEB"},
                new int[]{R.id.entryReport, R.id.dateReport, R.id.credDebReport},
                0);
        listAdapter = listAdapter2;
        listEntries.setAdapter(listAdapter2);
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }
}
