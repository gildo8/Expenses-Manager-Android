package com.example.gil.expensesmanager.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by gildo on 02/07/2016.
 */
public class LastUpdateSql {
    final static String LAST_UPDATE_TABLE = "last_update";
    final static String LAST_UPDATE_TABLE_TNAME = "table_name";
    final static String LAST_UPDATE_TABLE_DATE = "date";

    static public void create(SQLiteDatabase db) {
        db.execSQL("create table " + LAST_UPDATE_TABLE + " (" +
                LAST_UPDATE_TABLE_TNAME + " TEXT PRIMARY KEY," +
                LAST_UPDATE_TABLE_DATE + " TEXT);" );
        Log.d("LastUpdateSql"," CreateTable");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + LAST_UPDATE_TABLE + ";");
        Log.d("LastUpdateSql"," DropTable");
    }

    public static String getLastUpdate(SQLiteDatabase db, String tableName) {
        String[] args = {tableName};
        Cursor cursor = db.query(LAST_UPDATE_TABLE, null, LAST_UPDATE_TABLE_TNAME + " = ?",args , null, null, null);

        if (cursor.moveToFirst()) {
            Log.d("LastUpdateSql"," getLastUpdateSql");
            return cursor.getString(cursor.getColumnIndex(LAST_UPDATE_TABLE_DATE));
        }
        return null;
    }

    public static void setLastUpdate(SQLiteDatabase db, String table, String date) {
        ContentValues values = new ContentValues();
        values.put(LAST_UPDATE_TABLE_TNAME, table);
        values.put(LAST_UPDATE_TABLE_DATE, date);

        db.insertWithOnConflict(LAST_UPDATE_TABLE,LAST_UPDATE_TABLE_TNAME,values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("LastUpdateSql"," setLastUpdateSql");
    }
}
