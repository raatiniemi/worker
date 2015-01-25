package me.raatiniemi.worker.mapper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    abstract protected DomainObject load(Cursor row);
}
