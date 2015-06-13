package me.raatiniemi.worker.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SelectionBuilder {
    private String mTable;

    private String[] mProjection;

    private String mSelection;

    private String[] mSelectionArgs;

    private String mGroupBy;

    private String mHaving;

    private String mOrderBy;

    public String getTable() {
        return mTable;
    }

    public SelectionBuilder setTable(String table) {
        mTable = table;
        return this;
    }

    public String[] getProjection() {
        return mProjection;
    }

    public SelectionBuilder setProjection(String[] projection) {
        mProjection = projection;
        return this;
    }

    public String getSelection() {
        return mSelection;
    }

    public SelectionBuilder setSelection(String selection) {
        mSelection = selection;
        return this;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public SelectionBuilder setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
        return this;
    }

    public String getGroupBy() {
        return mGroupBy;
    }

    public SelectionBuilder setGroupBy(String groupBy) {
        mGroupBy = groupBy;
        return this;
    }

    public String getHaving() {
        return mHaving;
    }

    public SelectionBuilder setHaving(String having) {
        mHaving = having;
        return this;
    }

    public String getOrderBy() {
        return mOrderBy;
    }

    public SelectionBuilder setOrderBy(String orderBy) {
        mOrderBy = orderBy;
        return this;
    }

    public Cursor query(SQLiteDatabase db) {
        return db.query(
            getTable(),
            getProjection(),
            getSelection(),
            getSelectionArgs(),
            getGroupBy(),
            getHaving(),
            getOrderBy()
        );
    }
}
