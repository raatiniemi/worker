package me.raatiniemi.worker.application;

import android.app.Application;
import android.content.Context;

public class Worker extends Application
{
    private static Context mContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext()
    {
        return mContext;
    }
}
