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

    public void setTable(String table) {
        mTable = table;
    }

    public String[] getProjection() {
        return mProjection;
    }

    public void setProjection(String[] projection) {
        mProjection = projection;
    }

    public String getSelection() {
        return mSelection;
    }

    public void setSelection(String selection) {
        mSelection = selection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    public String getGroupBy() {
        return mGroupBy;
    }

    public void setGroupBy(String groupBy) {
        mGroupBy = groupBy;
    }

    public String getHaving() {
        return mHaving;
    }

    public void setHaving(String having) {
        mHaving = having;
    }

    public String getOrderBy() {
        return mOrderBy;
    }

    public void setOrderBy(String orderBy) {
        mOrderBy = orderBy;
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
