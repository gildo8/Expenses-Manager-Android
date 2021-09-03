package com.example.gil.expensesmanager.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.gil.expensesmanager.MyApplication;

import java.util.List;

/**
 * Created by gildo on 27/05/2016.
 */
public class ModelSql {
    private static final int VERSION = 2;
    MyDBHelper dbHelper;

    public ModelSql(Context context) {
        dbHelper = new MyDBHelper(context);
    }

    public SQLiteDatabase getWritableDB(){
        Log.d("ModelSql"," GetWritableDb");
        return dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadbleDB(){
        Log.d("ModelSql"," GetReadbleDb");
        return dbHelper.getReadableDatabase();
    }

    public void CreateDb(SQLiteDatabase db){
        Log.d("ModelSql"," CreateDB");
        UserSql.create(db);
        ItemSql.create(db);
        LastUpdateSql.create(db);
    }

    public void DropDb(SQLiteDatabase db){
        Log.d("ModelSql"," DropDb");
        UserSql.drop(db);
        ItemSql.drop(db);
        LastUpdateSql.drop(db);
    }

    public class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, "database.db", null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //create the DB schema
            Log.d("MyDBHelper"," OnCreate");
            UserSql.create(db);
            ItemSql.create(db);
            LastUpdateSql.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("MyDBHelper"," OnUpgrade");
            UserSql.drop(db);
            ItemSql.drop(db);
            LastUpdateSql.drop(db);
            onCreate(db);
        }
    }
}
