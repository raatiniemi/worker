package me.raatiniemi.worker.database;

import android.database.sqlite.SQLiteDatabase;

public class ProjectDataSource
{
    private static final String TABLE_NAME = "project";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";

    public static final String CREATE_TABLE_PROJECT =
        "CREATE TABLE " + TABLE_NAME + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT NOT NULL " +
        ");";

    protected Helper mHelper;

    protected SQLiteDatabase mDatabase;

    public ProjectDataSource(Helper helper) {
        mHelper = helper;
        mDatabase = mHelper.getWritableDatabase();
    }
}
