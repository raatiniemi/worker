package me.raatiniemi.worker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.raatiniemi.worker.application.Worker;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;

/**
 * Database helper for the "worker"-database.
 */
public class Helper extends SQLiteOpenHelper
{
    /**
     * Name of the database.
     */
    private static final String DATABASE_NAME = "worker";

    /**
     * Version of the database structure.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Instance for the database helper.
     */
    private static Helper mHelper;

    /**
     * Retrieve the instance for the database helper, instantiate if necessary.
     * @return Instance for the database helper.
     */
    public static synchronized Helper getInstance()
    {
        if ( mHelper == null ) {
            mHelper = new Helper(Worker.getContext());
        }
        return mHelper;
    }

    /**
     * Instantiate the database helper with the application context.
     * @param context Application context.
     */
    public Helper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(ProjectMapper.CREATE_TABLE);
        db.execSQL(TimeMapper.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}
