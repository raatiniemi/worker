/*
 * Copyright (C) 2017 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.data.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.raatiniemi.util.NullUtil.nonNull;

public class SelectionBuilder {
    private String table;

    private final StringBuilder selection = new StringBuilder();

    private final List<String> selectionArgs = new ArrayList<>();

    private String groupBy;

    private String having;

    public SelectionBuilder table(String table) {
        this.table = table;
        return this;
    }

    public SelectionBuilder where(String selection, String... selectionArgs) {
        // If the selection is empty, we can continue.
        if (TextUtils.isEmpty(selection)) {
            return this;
        }

        // If we are using multiple selections we need to
        // match all of the selections.
        if (0 < this.selection.length()) {
            this.selection.append(" AND ");
        }

        // In case we are using multiple selections we have
        // to encapsulate each of the selections.
        this.selection.append("(").append(selection).append(")");
        if (nonNull(selectionArgs)) {
            Collections.addAll(this.selectionArgs, selectionArgs);
        }
        return this;
    }

    private String selection() {
        return selection.toString();
    }

    private String[] selectionArgs() {
        return selectionArgs.toArray(new String[selectionArgs.size()]);
    }

    public SelectionBuilder groupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public SelectionBuilder having(String having) {
        this.having = having;
        return this;
    }

    public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
        return query(db, columns, orderBy, null);
    }

    public Cursor query(SQLiteDatabase db, String[] columns, String orderBy, String limit) {
        return db.query(
                table,
                columns,
                selection(),
                selectionArgs(),
                groupBy,
                having,
                orderBy,
                limit
        );
    }

    public int update(SQLiteDatabase db, ContentValues values) {
        return db.update(
                table,
                values,
                selection(),
                selectionArgs()
        );
    }

    public int delete(SQLiteDatabase db) {
        return db.delete(table, selection(), selectionArgs());
    }
}
