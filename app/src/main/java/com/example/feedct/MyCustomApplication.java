package com.example.feedct;

import android.app.Application;

public class MyCustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new DataManager(getResources());
    }
}
