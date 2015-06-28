package me.raatiniemi.worker.mapper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import me.raatiniemi.worker.domain.DomainObject;
import me.raatiniemi.worker.provider.WorkerDatabase;

/**
 * Abstract class for the data mappers.
 */
abstract public class AbstractMapper<T extends DomainObject> {
    /**
     * Instance for the database helper.
     */
    protected WorkerDatabase mWorkerDatabase;

    /**
     * Instance for the database.
     */
    protected SQLiteDatabase mDatabase;

    /**
     * Instantiate the data mapper with the worker database.
     *
     * @param workerDatabase Worker database.
     */
    public AbstractMapper(WorkerDatabase workerDatabase) {
        mWorkerDatabase = workerDatabase;
        mDatabase = mWorkerDatabase.getWritableDatabase();
    }

    /**
     * Instantiate the data mapper, uses the singleton database helper.
     */
    public AbstractMapper() {
        this(WorkerDatabase.getInstance());
    }
}
