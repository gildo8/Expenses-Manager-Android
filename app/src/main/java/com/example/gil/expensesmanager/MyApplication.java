package com.example.gil.expensesmanager;

import android.app.Application;
import android.content.Context;

/**
 * Created by gildo on 21/05/2016.
 */
public class MyApplication extends Application {
    private static Context context;
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
    public static Context getAppContext() {
        return MyApplication.context;
    }
}
