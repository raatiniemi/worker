package me.raatiniemi.worker.util;

import android.content.ContentValues;
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

    public SelectionBuilder table(String table) {
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

    public String selection() {
        return mSelection.toString();
    }

    public String[] selectionArgs() {
        return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
    }

    public SelectionBuilder groupBy(String groupBy) {
        mGroupBy = groupBy;
        return this;
    }

    public SelectionBuilder having(String having) {
        mHaving = having;
        return this;
    }

    public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
        return db.query(
            mTable,
            columns,
            selection(),
            selectionArgs(),
            mGroupBy,
            mHaving,
            orderBy
        );
    }

    public int update(SQLiteDatabase db, ContentValues values) {
        return db.update(
            mTable,
            values,
            selection(),
            selectionArgs()
        );
    }

    public int delete(SQLiteDatabase db) {
        return db.delete(mTable, selection(), selectionArgs());
    }
}
