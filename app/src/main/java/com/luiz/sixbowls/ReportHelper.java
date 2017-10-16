package com.luiz.sixbowls;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by Luiz on 16/10/2017.
 */

public class ReportHelper extends ListActivity {

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ListView listEntries = getListView();

        try{
            SQLiteOpenHelper dbHelper = new SixBowlsDbHelper(this);
            db = dbHelper.getReadableDatabase();

            cursor = db.query("INOUT",
                    new String[]{"_id", "ENTRY"},
                    null, null, null, null, null);

            CursorAdapter listAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"ENTRY"},
                    new int[]{android.R.id.text1},
                    0);
            listEntries.setAdapter(listAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "DB unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
