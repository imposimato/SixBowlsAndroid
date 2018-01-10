package com.luiz.sixbowls;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;


public class BalanceUpdate {

    double totalCred = 0;
    double totalDeb = 0;
    SixBowlsDbHelper dbHelper = new SixBowlsDbHelper(StaticContext.getAppContext());
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    public void updateBalance(String date1, String date2) {

        String sumCredQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND CREDDEB = 'C'";
        String sumDebQuery = "SELECT SUM(ENTRY) AS TOTAL FROM INOUT WHERE (DATE BETWEEN ? AND ?) AND CREDDEB = 'D'";

        Cursor cursorSumCred = db.rawQuery(sumCredQuery, new String[]{date1, date2});
        Cursor cursorSumDeb = db.rawQuery(sumDebQuery, new String[]{date1, date2});

        if (cursorSumCred.moveToFirst()) {
            totalCred = cursorSumCred.getDouble(cursorSumCred.getColumnIndex("TOTAL"));
            Reports.resultCredTV.setText("Total Credit: " + String.format("%.2f", totalCred));
        }

        if (cursorSumDeb.moveToFirst()) {
            totalDeb = cursorSumDeb.getDouble(cursorSumCred.getColumnIndex("TOTAL"));
            Reports.resultDebTV.setText("Total Debit: " + String.format("%.2f", totalDeb));
        }

        if (totalCred - totalDeb < 0) {
            Reports.balanceTV.setTextColor(Color.RED);
        } else {
            Reports.balanceTV.setTextColor(Color.GREEN);
        }

        Reports.balanceTV.setText("Balance : " + String.format("%.2f", (totalCred - totalDeb)));

    }
}
