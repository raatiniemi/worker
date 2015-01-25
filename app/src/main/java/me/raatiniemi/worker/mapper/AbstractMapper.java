package me.raatiniemi.worker.mapper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import me.raatiniemi.worker.database.Helper;
import me.raatiniemi.worker.domain.DomainObject;

abstract public class AbstractMapper
{
    protected Helper mHelper;

    protected SQLiteDatabase mDatabase;

    public AbstractMapper(Helper helper)
    {
        mHelper = helper;
        mDatabase = mHelper.getWritableDatabase();
    }

    public AbstractMapper(Context context)
    {
        this(Helper.getInstance(context));
    }

    abstract protected String getTable();

    abstract protected String[] getColumns();

    abstract protected DomainObject load(Cursor row);

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
