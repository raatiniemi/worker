package me.raatiniemi.worker.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectionBuilder {
    private String mTable;

    private StringBuilder mSelection = new StringBuilder();

    private List<String> mSelectionArgs = new ArrayList<>();

    private String mGroupBy;

    private String mHaving;

    public String getTable() {
        return mTable;
    }

    public SelectionBuilder setTable(String table) {
        mTable = table;
        return this;
    }

    public SelectionBuilder where(String selection, String... selectionArgs) {
        // If the selection is empty, we can continue.
        if (TextUtils.isEmpty(selection)) {
            return this;
        }

        // If we are using multiple selections we need to
        // match all of the selections.
        if (0 < mSelection.length()) {
            mSelection.append(" AND ");
        }

        // In case we are using multiple selections we have
        // to encapsulate each of the selections.
        mSelection.append("(").append(selection).append(")");
        if (null != selectionArgs) {
            Collections.addAll(mSelectionArgs, selectionArgs);
        }
        return this;
    }

    public String getSelection() {
        return mSelection.toString();
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
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

    public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
        return db.query(
            getTable(),
            columns,
            getSelection(),
            getSelectionArgs(),
            getGroupBy(),
            getHaving(),
            orderBy
        );
    }
}
