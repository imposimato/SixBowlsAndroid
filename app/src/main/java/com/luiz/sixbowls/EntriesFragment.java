package com.luiz.sixbowls;


import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
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
    private List<String> mResults;
    private Cursor cursor;
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


            cursor = db.query("INOUT",
                    new String[]{"_id", "ENTRY", "DATE", "CREDDEB"},
                    null, null, null, null, null);
            // Init
            init(view);
        } catch (Exception e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void init(View v) {

        listEntries = getListView();
        // Setup the listAdapter
        CursorAdapter listAdapter = new android.widget.SimpleCursorAdapter(mContext,
                R.layout.fragment_item,
                cursor,
                new String[]{"ENTRY", "DATE", "CREDDEB"},
                new int[]{R.id.entryReport, R.id.dateReport, R.id.credDebReport},
                0);
        listEntries.setAdapter(listAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        Toast.makeText(mContext, "CLICKED ON POS #" + pos + "!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
