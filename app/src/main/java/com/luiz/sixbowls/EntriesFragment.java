package com.luiz.sixbowls;


import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class EntriesFragment extends ListFragment {

    // Attributes
    private Context mContext;
    private Cursor cursor;
    private String date1, date2;
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    // Elements
    private ListView listEntries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();

        // Let's inflate & return the view
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Return
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // Get the database handler & the cursor
        try{
            dbHelper = new SixBowlsDbHelper(mContext);
            db = dbHelper.getWritableDatabase();
            //TODO Filtrar data de acordo com user
            //TODO Soma do Cred/Deb
            cursor = db.query("INOUT",
                    new String[]{"_id", "ENTRY", "printf('%.2f', ENTRY) as ENTRYF",
                            "DATE", "strftime('%d/%m/%Y', DATE) as DATEF", "CREDDEB"},
                    null, null, null, null, null);
            // Init
            init(view);
        } catch (Exception e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void init(View v) {

        listEntries = getListView();
        // Setup the listAdapter
        CursorAdapter listAdapter = new android.widget.SimpleCursorAdapter(mContext,
                R.layout.fragment_item,
                cursor,
                new String[]{"ENTRYF", "DATEF", "CREDDEB"},
                new int[]{R.id.entryReport, R.id.dateReport, R.id.credDebReport},
                0);
        listEntries.setAdapter(listAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        Toast.makeText(mContext, "CLICKED ON POS #" + pos + "!", Toast.LENGTH_SHORT).show();
        //TODO Excluir entrada BD
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    public void setDate1(int date1) {
        String aux = String.valueOf(date1);
        this.date1 = aux.substring(6,8) + "-" + aux.substring(4,6) + "-" + aux.substring(0,4);
    }

    public void setDate2(int date2) {
        String aux = String.valueOf(date2);
        this.date2 = aux.substring(6,8) + "-" + aux.substring(4,6) + "-" + aux.substring(0,4);
    }
}
