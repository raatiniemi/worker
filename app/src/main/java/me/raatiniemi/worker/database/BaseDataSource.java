package me.raatiniemi.worker.database;

import android.database.sqlite.SQLiteDatabase;

public class BaseDataSource
{
    protected Helper mHelper;

    protected SQLiteDatabase mDatabase;

    public BaseDataSource(Helper helper)
    {
        mHelper = helper;
        mDatabase = mHelper.getWritableDatabase();
    }
}
