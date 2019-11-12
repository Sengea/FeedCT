package com.example.feedct;

import android.app.Application;

public class MyCustomApplcation extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new JSONManager(getResources());
    }
}
