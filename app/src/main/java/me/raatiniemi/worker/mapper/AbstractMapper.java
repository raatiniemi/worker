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

    /**
     * Retrieve the table name connected to the data mapper.
     *
     * @return Table name connected to the data mapper.
     */
    abstract protected String getTable();

    /**
     * Retrieve the column names for the table.
     *
     * @return Column names for the table.
     */
    abstract protected String[] getColumns();

    /**
     * Load the DomainObject from the data mapper with the database cursor.
     *
     * @param row Database cursor.
     * @return DomainObject from the data mapper.
     */
    abstract protected T load(Cursor row);
}
