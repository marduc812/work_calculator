package com.marduc812.workcalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marduc on 4/11/14.
 */
public class DBhelper extends SQLiteOpenHelper {

    // TABLE INFORMATTION
    public static final String TABLE_NAME = "WorkEventTable";
    public static final String UID = "_id";
    public static final String DAY = "day";
    public static final String MONTHYEAR = "monthyear";
    public static final String STARTED = "started";
    public static final String ELAPSED = "elapsed";
    public static final String INCOME = "income";


    // DATABASE INFORMATION
    static final String DB_NAME = "WORK_EVENT.DB";
    static final int DB_VERSION = 2;

    // TABLE CREATION STATEMENT
    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME + "(" + UID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DAY + " TEXT NOT NULL, "
            + MONTHYEAR + " TEXT NOT NULL, "
            + STARTED + " TEXT NOT NULL, "
            + ELAPSED + " TEXT NOT NULL, "
            + INCOME + " TEXT NOT NULL);";

    public DBhelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


}