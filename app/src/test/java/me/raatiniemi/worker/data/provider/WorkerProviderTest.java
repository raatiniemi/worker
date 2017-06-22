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

package me.raatiniemi.worker.data.provider;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.RobolectricTestCase;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WorkerProviderTest extends RobolectricTestCase {
    private SQLiteDatabase database;
    private WorkerProvider provider;

    @Before
    public void setUp() {
        database = mock(SQLiteDatabase.class);

        WorkerDatabase helper = mock(WorkerDatabase.class);
        when(helper.getReadableDatabase())
                .thenReturn(database);
        when(helper.getWritableDatabase())
                .thenReturn(database);

        provider = new WorkerProvider();
        provider.openHelper = helper;
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_unknownUri() {
        Uri uri = new Uri.Builder()
                .path("/unknown/uri")
                .build();

        provider.getType(uri);
    }

    @Test
    public void getType_withProjectsStreamUri() {
        String mimeType = provider.getType(ProviderContract.getProjectStreamUri());

        assertEquals(ProviderContract.TYPE_STREAM_PROJECT, mimeType);
    }

    @Test
    public void getType_withProjectsIdUri() {
        String mimeType = provider.getType(ProviderContract.getProjectItemUri(1));

        assertEquals(ProviderContract.TYPE_ITEM_PROJECT, mimeType);
    }

    @Test
    public void getType_withProjectsTimeUri() {
        String mimeType = provider.getType(ProviderContract.getProjectItemTimeUri(1));

        assertEquals(ProviderContract.TYPE_STREAM_TIME, mimeType);
    }

    @Test
    public void getType_withProjectsTimesheetUri() {
        String mimeType = provider.getType(ProviderContract.getTimesheetStreamUri(1));

        assertEquals(ProviderContract.TYPE_STREAM_TIME, mimeType);
    }

    @Test
    public void getType_withTimeStreamUri() {
        String mimeType = provider.getType(ProviderContract.getTimeStreamUri());

        assertEquals(ProviderContract.TYPE_STREAM_TIME, mimeType);
    }

    @Test
    public void getType_withTimeItemUri() {
        String mimeType = provider.getType(ProviderContract.getTimeItemUri(1));

        assertEquals(ProviderContract.TYPE_ITEM_TIME, mimeType);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void query_withUnknownUri() {
        provider.query(
                new Uri.Builder()
                        .path("/unknown/uri")
                        .build(),
                new String[]{},
                "",
                new String[]{},
                ""
        );
    }

    @Test
    public void query_projects() {
        provider.query(
                ProviderContract.getProjectStreamUri(),
                ProviderContract.getProjectColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(ProviderContract.TABLE_PROJECT),
                eq(ProviderContract.getProjectColumns()),
                eq(""),
                isA(String[].class),
                eq(null),
                eq(null),
                eq(""),
                eq(null)
        );
    }

    @Test
    public void query_project() {
        provider.query(
                ProviderContract.getProjectItemUri(1),
                ProviderContract.getProjectColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(ProviderContract.TABLE_PROJECT),
                eq(ProviderContract.getProjectColumns()),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"}),
                eq(null),
                eq(null),
                eq(""),
                eq(null)
        );
    }

    @Test
    public void query_projectTime() {
        provider.query(
                ProviderContract.getProjectItemTimeUri(1),
                ProviderContract.getTimeColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(ProviderContract.TABLE_TIME),
                eq(ProviderContract.getTimeColumns()),
                eq("(" + ProviderContract.COLUMN_TIME_PROJECT_ID + "=?)"),
                eq(new String[]{"1"}),
                eq(null),
                eq(null),
                eq(""),
                eq(null)
        );
    }

    @Test
    public void query_projectTimesheet() {
        provider.query(
                ProviderContract.getTimesheetStreamUri(1),
                ProviderContract.getTimesheetStreamColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(ProviderContract.TABLE_TIME),
                eq(ProviderContract.getTimesheetStreamColumns()),
                eq("(" + ProviderContract.COLUMN_TIME_PROJECT_ID + "=?)"),
                eq(new String[]{"1"}),
                eq(ProviderContract.GROUP_BY_TIMESHEET),
                eq(null),
                eq(""),
                eq(null)
        );
    }

    @Test
    public void query_time() {
        provider.query(
                ProviderContract.getTimeItemUri(1),
                ProviderContract.getTimeColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(ProviderContract.TABLE_TIME),
                eq(ProviderContract.getTimeColumns()),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"}),
                eq(null),
                eq(null),
                eq(""),
                eq(null)
        );
    }

    @Test
    public void query_withLimit() {
        provider.query(
                ProviderContract.getProjectStreamUri()
                        .buildUpon()
                        .appendQueryParameter(QueryParameter.LIMIT, "1")
                        .build(),
                ProviderContract.getProjectColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(ProviderContract.TABLE_PROJECT),
                eq(ProviderContract.getProjectColumns()),
                eq(""),
                isA(String[].class),
                eq(null),
                eq(null),
                eq(""),
                eq("0,1")
        );
    }

    @Test
    public void query_withLimitAndOffset() {
        provider.query(
                ProviderContract.getProjectStreamUri()
                        .buildUpon()
                        .appendQueryParameter(QueryParameter.OFFSET, "10")
                        .appendQueryParameter(QueryParameter.LIMIT, "5")
                        .build(),
                ProviderContract.getProjectColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(ProviderContract.TABLE_PROJECT),
                eq(ProviderContract.getProjectColumns()),
                eq(""),
                isA(String[].class),
                eq(null),
                eq(null),
                eq(""),
                eq("10,5")
        );
    }

    @Test
    public void insert_withNoneStreamUris() {
        List<Uri> uris = new ArrayList<>();
        uris.add(ProviderContract.getProjectItemUri(1));
        uris.add(ProviderContract.getProjectItemTimeUri(1));
        uris.add(ProviderContract.getTimesheetStreamUri(1));
        uris.add(ProviderContract.getTimeItemUri(1));
        ContentValues values = new ContentValues();

        String format = "Non-stream URI \"%s\" did not throw exception";
        for (Uri uri : uris) {
            try {
                provider.insert(uri, values);
                fail(String.format(format, uri.toString()));
            } catch (UnsupportedOperationException e) {
                // if an exception have been thrown that iteration
                // of the test is successful.
            }
        }
    }

    @Test
    public void insert_withProjectsStreamUri() {
        ContentValues values = new ContentValues();
        when(database.insertOrThrow(eq(ProviderContract.TABLE_PROJECT), eq(null), eq(values)))
                .thenReturn(1L);

        Uri uri = provider.insert(ProviderContract.getProjectStreamUri(), values);

        assertEquals(ProviderContract.getProjectItemUri(1), uri);
    }

    @Test
    public void insert_withTimeStreamUri() {
        ContentValues values = new ContentValues();
        when(database.insertOrThrow(eq(ProviderContract.TABLE_TIME), eq(null), eq(values)))
                .thenReturn(1L);

        Uri uri = provider.insert(ProviderContract.getTimeStreamUri(), values);

        assertEquals(ProviderContract.getTimeItemUri(1), uri);
    }

    @Test
    public void update_projectItem() {
        ContentValues values = new ContentValues();

        provider.update(
                ProviderContract.getProjectItemUri(1),
                values,
                null,
                null
        );

        verify(database).update(
                eq(ProviderContract.TABLE_PROJECT),
                eq(values),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"})
        );
    }

    @Test
    public void update_timeItem() {
        ContentValues values = new ContentValues();

        provider.update(
                ProviderContract.getTimeItemUri(1),
                values,
                null,
                null
        );

        verify(database).update(
                eq(ProviderContract.TABLE_TIME),
                eq(values),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"})
        );
    }

    @Test
    public void delete_projectItem() {
        provider.delete(
                ProviderContract.getProjectItemUri(1),
                null,
                null
        );

        verify(database).delete(
                eq(ProviderContract.TABLE_PROJECT),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"})
        );
    }

    @Test
    public void delete_timeItem() {
        provider.delete(
                ProviderContract.getTimeItemUri(1),
                null,
                null
        );

        verify(database).delete(
                eq(ProviderContract.TABLE_TIME),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"})
        );
    }

    @Test
    public void applyBatch_withoutOperations()
            throws OperationApplicationException {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        provider.applyBatch(operations);

        verify(database).beginTransaction();
        verify(database).setTransactionSuccessful();
        verify(database).endTransaction();
    }
}
