package com.luiz.sixbowls;

import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EntriesFragment extends ListFragment {

    // Attributes
    private Context mContext;
    private Cursor cursor;
    private String date1, date2;
    String note;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    protected boolean aux;
    CursorAdapter listAdapter;
    BalanceUpdate balanceUpdate;

    // Elements
    private ListView listEntries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our context
        mContext = getActivity();

        // Set up BalanceUpdateClass
        balanceUpdate = new BalanceUpdate();

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

            // Erases and entry on Long Press an item from the listview
            listEntries.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, final long id) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Delete Entry?")
                            .setMessage("Do you really want to delete this entry?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    aux = true;
                                    try {
                                        db.execSQL("DELETE FROM INOUT WHERE _id = '" + String.valueOf(id) + "'");
                                        updateCursor();
                                        balanceUpdate.updateBalance(date1, date2);
                                    } catch (SQLException e) {
                                        Toast.makeText(mContext, "Database unavailable", Toast.LENGTH_SHORT).show();
                                    }
                                    aux = false;
                                }})
                            .setNegativeButton(android.R.string.no, null)
                            .create().show();

                    return true;
                }
            });
            // Displays the Note added by the user to the entry
            listEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursorNote = db.rawQuery("SELECT NOTE FROM INOUT WHERE _id = '" + String.valueOf(id) +"'", null);
                    if (cursorNote.moveToFirst()) {
                        note = cursorNote.getString(cursorNote.getColumnIndex("NOTE"));
                    }
                    new AlertDialog.Builder(mContext)
                            .setTitle("Note:")
                            .setMessage(note)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(android.R.string.yes, null)
                            .create().show();
                }
            });

        } catch (Exception e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void updateCursor(){
        cursor = db.query("INOUT",
                new String[]{"_id", "ENTRY", "printf('%.2f', ENTRY) as ENTRYF",
                        "BOWL", "DATE", "strftime('%d/%m/%Y', DATE) as DATEF", "CREDDEB"},
                "DATE BETWEEN ? AND ?", new String[] {date1, date2},
                null, null, "DATE DESC");
        listAdapter = new android.widget.SimpleCursorAdapter(mContext,
                R.layout.fragment_item,
                cursor,
                new String[]{"DATEF", "BOWL", "ENTRYF", "CREDDEB"},
                new int[]{R.id.dateReport, R.id.bowlReport, R.id.entryReport, R.id.credDebReport},
                0);
        listEntries.setAdapter(listAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }
}
