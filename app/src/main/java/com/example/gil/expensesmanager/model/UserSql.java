package com.example.gil.expensesmanager.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gildo on 27/05/2016.
 */
public class UserSql {
    private static final String USERS_TABLE = "users";

    private static final String USER_ID = "uid";
    private static final String USER_NAME = "uname";
    private static final String USER_STARTING_BUDGET = "ustartingbudget";
    private static final String USER_CURRENT_BUDGET = "ucurrentbudget";

    public static void addNewUser(SQLiteDatabase db,User user) {
        ContentValues values = new ContentValues();

        values.put(USER_ID, user.getId());
        values.put(USER_NAME, user.getName());
        values.put(USER_CURRENT_BUDGET,"0");
        values.put(USER_STARTING_BUDGET,"0");

        db.insert(USERS_TABLE, USER_ID, values);
        Log.d("UserSql"," AddNewUser " + user.getName());
    }

    public static User getSingleUser(SQLiteDatabase db, String uid){
        String[] params = new String[1];
        params[0] = uid;
        Cursor cursor = db.query(USERS_TABLE, null, USER_ID + " = " + uid, params, null, null, null);

        if (cursor.moveToFirst()) {
            int userId = cursor.getColumnIndex("USER_ID");
            int userName = cursor.getColumnIndex("USER_NAME");
            int userStartingBudget = cursor.getColumnIndex("USER_STARTING_BUDGET");
            int userCurrentBudget = cursor.getColumnIndex("USER_CURRENT_BUDGET");

            String uId = cursor.getString(userId);
            String uName = cursor.getString(userName);
            float uStartingBudget = cursor.getFloat(userStartingBudget);
            float uCurrentBudget = cursor.getFloat(userCurrentBudget);

            User user = new User(uId,uName,uStartingBudget,uCurrentBudget);
            Log.d("UserSql"," GetSingleUser");
            return user;
        }
        return null;
    }

    public static List<User> getAllUsers (SQLiteDatabase db) {
        Cursor cursor = db.query(USERS_TABLE, null, null, null, null, null, null);

        List<User> list = new LinkedList<User>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(USER_ID);
            int uNameIndex = cursor.getColumnIndex(USER_NAME);
            int startingBudgetIndex = cursor.getColumnIndex(USER_STARTING_BUDGET);
            int currentBudgetIndex = cursor.getColumnIndex(USER_CURRENT_BUDGET);
            do {
                String id = cursor.getString(idIndex);
                String name = cursor.getString(uNameIndex);
                float uStartingBudget = cursor.getFloat(startingBudgetIndex);
                float uCurrentBudget = cursor.getFloat(currentBudgetIndex);

                User st = new User(id,name,uStartingBudget,uCurrentBudget);
                list.add(st);
            } while (cursor.moveToNext());
            Log.d("UserSql"," GetAllUsers");
        }
        return list;
    }


    static public void create(SQLiteDatabase db) {
        db.execSQL("create table " +
                USERS_TABLE + " (" +
                USER_ID + " TEXT PRIMARY KEY," +
                USER_NAME + " TEXT," +
                USER_STARTING_BUDGET + " TEXT," +
                USER_CURRENT_BUDGET + " TEXT);");
        Log.d("UserSql"," Create Table");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + USERS_TABLE);
        Log.d("UserSql"," Drop Table");
    }
}
