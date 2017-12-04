package com.luiz.sixbowls;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SixBowlsDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "sixBowls";
    private static final int DB_VERSION = 3;

    public SixBowlsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    public void SixBowlsDbHelper(){

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDB(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDB(db, oldVersion, newVersion);
    }

    public void updateMyDB(SQLiteDatabase db, int oldVersion, int newVersion){

        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE INOUT (_id INTEGER PRIMARY KEY AUTOINCREMENT, ENTRY REAL);");
        }
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE INOUT ADD COLUMN DATE INTEGER");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE INOUT ADD COLUMN CREDDEB TEXT");
        }
    }

    public void insertEntryDb(SQLiteDatabase db, double entry){
        ContentValues entryValue = new ContentValues();
        entryValue.put("ENTRY", entry);
        db.insert("INOUT", null, entryValue);
    }

    public static void excludeEntry(SQLiteDatabase db, long id){
        String string =String.valueOf(id);
        db.execSQL("DELETE FROM favorite WHERE _id = '" + string + "'");
    }


}
