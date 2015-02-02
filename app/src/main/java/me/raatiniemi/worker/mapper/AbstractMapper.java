package me.raatiniemi.worker.mapper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import me.raatiniemi.worker.database.Helper;
import me.raatiniemi.worker.domain.DomainObject;

/**
 * Abstract class for the data mappers.
 */
abstract public class AbstractMapper
{
    /**
     * Instance for the database helper.
     */
    protected Helper mHelper;

    /**
     * Instance for the database.
     */
    protected SQLiteDatabase mDatabase;

    /**
     * Instantiate the data mapper with the database helper.
     * @param helper Database helper.
     */
    public AbstractMapper(Helper helper)
    {
        mHelper = helper;
        mDatabase = mHelper.getWritableDatabase();
    }

    /**
     * Instantiate the data mapper, uses the singleton database helper.
     */
    public AbstractMapper()
    {
        this(Helper.getInstance());
    }

    /**
     * Retrieve the table name connected to the data mapper.
     * @return Table name connected to the data mapper.
     */
    abstract protected String getTable();

    /**
     * Retrieve the column names for the table.
     * @return Column names for the table.
     */
    abstract protected String[] getColumns();

    /**
     * Load the DomainObject from the data mapper with the database cursor.
     * @param row Database cursor.
     * @return DomainObject from the data mapper.
     */
    abstract protected DomainObject load(Cursor row);

    /**
     * Retrieve a row from the database with supplied id.
     * @param id Id for the row to retrieve.
     * @return Row from the database with id matching the supplied value.
     */
    public DomainObject find(long id)
    {
        String selection = BaseColumns._ID + "=" + id;

        Cursor row = mDatabase.query(getTable(), getColumns(), selection, null, null, null, null, null);
        if (!row.moveToFirst()) {
            return null;
        }

        return load(row);
    }
}
