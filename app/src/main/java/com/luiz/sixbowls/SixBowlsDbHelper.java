package com.luiz.sixbowls;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SixBowlsDbHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "sixBowls";
    private static final int DB_VERSION = 4;

    public SixBowlsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    public void SixBowlsDbHelper(){

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE INOUT (_id INTEGER PRIMARY KEY AUTOINCREMENT, ENTRY DECIMAL(11,2), DATE INTEGER, CREDDEB TEXT, BOWL TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertEntry(SQLiteDatabase db, double entry){
        ContentValues entryValue = new ContentValues();
        entryValue.put("ENTRY", entry);
        db.insert("INOUT", null, entryValue);
    }

    public void deleteEntry(SQLiteDatabase db, long id){
        String string =String.valueOf(id);
        db.execSQL("DELETE FROM favorite WHERE _id = '" + string + "'");
    }


}
