package com.marduc812.workcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by marduc on 9/4/14.
 */
public class SQLController {

    private DBhelper dbhelper;
    private Context context;
    private SQLiteDatabase db;

    public SQLController(Context c) {
        context = c;
    }

    public SQLController open() throws SQLException {
        dbhelper = new DBhelper(context);
        db = dbhelper.getWritableDatabase();
        return this;

    }

    public void close() {
        dbhelper.close();
    }


    public void insertData(String day, String month,String started, String elapsed, String income) {

        ContentValues cv = new ContentValues();
        cv.put(dbhelper.DAY,day);
        cv.put(dbhelper.MONTHYEAR,month);
        cv.put(DBhelper.STARTED, started);
        cv.put(DBhelper.ELAPSED, elapsed);
        cv.put(DBhelper.INCOME,income);
        db.insert(DBhelper.TABLE_NAME, null, cv);
    }


    public void deleteData(long memberID) {
        db.delete(DBhelper.TABLE_NAME, DBhelper.UID + "="
                + memberID, null);
    }

    public Cursor readData() {
        String[] allColumns = new String[] { DBhelper.UID ,DBhelper.DAY ,DBhelper.MONTHYEAR ,DBhelper.STARTED, DBhelper.ELAPSED, DBhelper.INCOME};
        Cursor c = db.query(DBhelper.TABLE_NAME, allColumns, null,null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }








}
