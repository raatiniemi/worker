package me.raatiniemi.worker.application;

import android.app.Application;
import android.content.Context;

public class Worker extends Application
{
    /**
     * Application context.
     */
    private static Context mContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = this;
    }

    /**
     * Retrieve the application context.
     * @return Application context.
     */
    public static Context getContext()
    {
        return mContext;
    }
}
