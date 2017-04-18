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
import me.raatiniemi.worker.data.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.provider.WorkerContract.TimeContract;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static me.raatiniemi.worker.data.provider.WorkerContract.Tables.PROJECT;
import static me.raatiniemi.worker.data.provider.WorkerContract.Tables.TIME;
import static me.raatiniemi.worker.data.provider.WorkerContract.TimeColumns.PROJECT_ID;
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
        String mimeType = provider.getType(ProjectContract.getStreamUri());

        assertEquals(ProjectContract.STREAM_TYPE, mimeType);
    }

    @Test
    public void getType_withProjectsIdUri() {
        String mimeType = provider.getType(ProjectContract.getItemUri(1));

        assertEquals(ProjectContract.ITEM_TYPE, mimeType);
    }

    @Test
    public void getType_withProjectsTimeUri() {
        String mimeType = provider.getType(ProjectContract.getItemTimeUri(1));

        assertEquals(TimeContract.STREAM_TYPE, mimeType);
    }

    @Test
    public void getType_withProjectsTimesheetUri() {
        String mimeType = provider.getType(ProjectContract.getItemTimesheetUri(1));

        assertEquals(TimeContract.STREAM_TYPE, mimeType);
    }

    @Test
    public void getType_withTimeStreamUri() {
        String mimeType = provider.getType(TimeContract.getStreamUri());

        assertEquals(TimeContract.STREAM_TYPE, mimeType);
    }

    @Test
    public void getType_withTimeItemUri() {
        String mimeType = provider.getType(TimeContract.getItemUri(1));

        assertEquals(TimeContract.ITEM_TYPE, mimeType);
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
                ProjectContract.getStreamUri(),
                ProjectContract.getColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(PROJECT),
                eq(ProjectContract.getColumns()),
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
                ProjectContract.getItemUri(1),
                ProjectContract.getColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(PROJECT),
                eq(ProjectContract.getColumns()),
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
                ProjectContract.getItemTimeUri(1),
                TimeContract.getColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(TIME),
                eq(TimeContract.getColumns()),
                eq("(" + PROJECT_ID + "=?)"),
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
                ProjectContract.getItemTimesheetUri(1),
                ProjectContract.getTimesheetColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(TIME),
                eq(ProjectContract.getTimesheetColumns()),
                eq("(" + PROJECT_ID + "=?)"),
                eq(new String[]{"1"}),
                eq(ProjectContract.GROUP_BY_TIMESHEET),
                eq(null),
                eq(""),
                eq(null)
        );
    }

    @Test
    public void query_time() {
        provider.query(
                TimeContract.getItemUri(1),
                TimeContract.getColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(TIME),
                eq(TimeContract.getColumns()),
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
                ProjectContract.getStreamUri()
                        .buildUpon()
                        .appendQueryParameter(QueryParameter.QUERY_PARAMETER_LIMIT, "1")
                        .build(),
                ProjectContract.getColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(PROJECT),
                eq(ProjectContract.getColumns()),
                eq(""),
                isA(String[].class),
                eq(null),
                eq(null),
                eq(""),
                eq("1")
        );
    }

    @Test
    public void query_withLimitAndOffset() {
        provider.query(
                ProjectContract.getStreamUri()
                        .buildUpon()
                        .appendQueryParameter(QueryParameter.QUERY_PARAMETER_OFFSET, "10")
                        .appendQueryParameter(QueryParameter.QUERY_PARAMETER_LIMIT, "5")
                        .build(),
                ProjectContract.getColumns(),
                "",
                new String[]{},
                ""
        );

        verify(database).query(
                eq(PROJECT),
                eq(ProjectContract.getColumns()),
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
        uris.add(ProjectContract.getItemUri(1));
        uris.add(ProjectContract.getItemTimeUri(1));
        uris.add(ProjectContract.getItemTimesheetUri(1));
        uris.add(TimeContract.getItemUri(1));
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
        when(database.insertOrThrow(eq(PROJECT), eq(null), eq(values)))
                .thenReturn(1L);

        Uri uri = provider.insert(ProjectContract.getStreamUri(), values);

        assertEquals(ProjectContract.getItemUri(1), uri);
    }

    @Test
    public void insert_withTimeStreamUri() {
        ContentValues values = new ContentValues();
        when(database.insertOrThrow(eq(TIME), eq(null), eq(values)))
                .thenReturn(1L);

        Uri uri = provider.insert(TimeContract.getStreamUri(), values);

        assertEquals(TimeContract.getItemUri(1), uri);
    }

    @Test
    public void update_projectItem() {
        ContentValues values = new ContentValues();

        provider.update(
                ProjectContract.getItemUri(1),
                values,
                null,
                null
        );

        verify(database).update(
                eq(PROJECT),
                eq(values),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"})
        );
    }

    @Test
    public void update_timeItem() {
        ContentValues values = new ContentValues();

        provider.update(
                TimeContract.getItemUri(1),
                values,
                null,
                null
        );

        verify(database).update(
                eq(TIME),
                eq(values),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"})
        );
    }

    @Test
    public void delete_projectItem() {
        provider.delete(
                ProjectContract.getItemUri(1),
                null,
                null
        );

        verify(database).delete(
                eq(PROJECT),
                eq("(" + BaseColumns._ID + "=?)"),
                eq(new String[]{"1"})
        );
    }

    @Test
    public void delete_timeItem() {
        provider.delete(
                TimeContract.getItemUri(1),
                null,
                null
        );

        verify(database).delete(
                eq(TIME),
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
