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

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.data.provider.ProviderContract.Tables;
import me.raatiniemi.worker.data.provider.ProviderContract.TimeColumns;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.util.Optional;

public class WorkerProvider extends ContentProvider {
    private static final int PROJECTS = 100;

    private static final int PROJECTS_ID = 101;

    private static final int PROJECTS_TIME = 102;

    private static final int PROJECTS_TIMESHEET = 103;

    private static final int TIME = 200;

    private static final int TIME_ID = 201;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    @Inject
    WorkerDatabase openHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProviderContract.AUTHORITY;

        matcher.addURI(authority, "projects", PROJECTS);
        matcher.addURI(authority, "projects/#", PROJECTS_ID);
        matcher.addURI(authority, "projects/#/time", PROJECTS_TIME);
        matcher.addURI(authority, "projects/#/timesheet", PROJECTS_TIMESHEET);

        matcher.addURI(authority, "time", TIME);
        matcher.addURI(authority, "time/#", TIME_ID);

        return matcher;
    }

    private synchronized WorkerDatabase getOpenHelper() {
        if (null == openHelper) {
            WorkerApplication.getInstance()
                    .getDataComponent()
                    .inject(this);
        }

        return openHelper;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        String mimeType;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                mimeType = ProviderContract.Project.STREAM_TYPE;
                break;
            case PROJECTS_ID:
                mimeType = ProviderContract.Project.ITEM_TYPE;
                break;
            case PROJECTS_TIME:
            case PROJECTS_TIMESHEET:
            case TIME:
                mimeType = ProviderContract.Time.STREAM_TYPE;
                break;
            case TIME_ID:
                mimeType = ProviderContract.Time.ITEM_TYPE;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return mimeType;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Selection select = buildSelection(uri, selection, selectionArgs);
        return getOpenHelper().getReadableDatabase()
                .query(
                        select.getTable(),
                        projection,
                        select.getSelection(),
                        select.getSelectionArgs(),
                        select.getGroupBy(),
                        null,
                        sortOrder,
                        parseLimitFromUri(uri)
                );
    }

    @Nullable
    private static String parseLimitFromUri(@NonNull Uri uri) {
        Optional<PageRequest> value = QueryParameter.extractPageRequestFromUri(uri);
        if (value.isPresent()) {
            PageRequest pageRequest = value.get();

            return String.format(
                    Locale.getDefault(),
                    "%d,%d",
                    pageRequest.getOffset(),
                    pageRequest.getMaxResults()
            );
        }

        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri createdResourceUri;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                createdResourceUri = insertProject(values);
                break;
            case TIME:
                createdResourceUri = insertTime(values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }

        return createdResourceUri;
    }

    private Uri insertProject(ContentValues values) {
        SQLiteDatabase db = getOpenHelper().getWritableDatabase();

        long id = db.insertOrThrow(Tables.PROJECT, null, values);
        return ProviderContract.Project.getItemUri(id);
    }

    private Uri insertTime(ContentValues values) {
        SQLiteDatabase db = getOpenHelper().getWritableDatabase();

        long id = db.insertOrThrow(Tables.TIME, null, values);
        return ProviderContract.Time.getItemUri(id);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Selection builder = buildSelection(uri, selection, selectionArgs);
        return getOpenHelper().getWritableDatabase()
                .update(
                        builder.getTable(),
                        values,
                        builder.getSelection(),
                        builder.getSelectionArgs()
                );
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Selection select = buildSelection(uri, selection, selectionArgs);
        return getOpenHelper().getWritableDatabase()
                .delete(
                        select.getTable(),
                        select.getSelection(),
                        select.getSelectionArgs()
                );
    }

    @Override
    @NonNull
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {

        final SQLiteDatabase db = getOpenHelper().getWritableDatabase();
        db.beginTransaction();
        try {
            final int numberOfOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numberOfOperations];
            for (int i = 0; i < numberOfOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();

            return results;
        } finally {
            db.endTransaction();
        }
    }

    private static Selection buildSelection(Uri uri, String selection, String[] selectionArgs) {
        Selection.Builder builder;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                builder = ProjectsSelection.build();
                break;
            case PROJECTS_ID:
                builder = ProjectSelection.build(uri);
                break;
            case PROJECTS_TIME:
                builder = ProjectTimeSelection.build(uri);
                break;
            case PROJECTS_TIMESHEET:
                builder = ProjectTimesheetSelection.build(uri);
                break;
            case TIME_ID:
                builder = TimeSelection.build(uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Unknown uri for selection: " + uri
                );
        }

        return builder.where(selection, selectionArgs).build();
    }

    private static class ProjectsSelection {
        private ProjectsSelection() {
        }

        private static Selection.Builder build() {
            return Selection.builder()
                    .table(Tables.PROJECT);
        }
    }

    private static class ProjectSelection {
        private ProjectSelection() {
        }

        private static Selection.Builder build(Uri uri) {
            return Selection.builder()
                    .table(Tables.PROJECT)
                    .where(
                            BaseColumns._ID + "=?",
                            ProviderContract.Project.getItemId(uri)
                    );
        }
    }

    private static class ProjectTimeSelection {
        private ProjectTimeSelection() {
        }

        private static Selection.Builder build(Uri uri) {
            return Selection.builder()
                    .table(Tables.TIME)
                    .where(
                            TimeColumns.PROJECT_ID + "=?",
                            ProviderContract.Project.getItemId(uri)
                    );
        }
    }

    private static class ProjectTimesheetSelection {
        private ProjectTimesheetSelection() {
        }

        private static Selection.Builder build(Uri uri) {
            return Selection.builder()
                    .table(Tables.TIME)
                    .where(
                            TimeColumns.PROJECT_ID + "=?",
                            ProviderContract.Project.getItemId(uri)
                    )
                    .groupBy(ProviderContract.Timesheet.GROUP_BY);
        }
    }

    private static class TimeSelection {
        private TimeSelection() {
        }

        private static Selection.Builder build(Uri uri) {
            return Selection.builder()
                    .table(Tables.TIME)
                    .where(
                            BaseColumns._ID + "=?",
                            ProviderContract.Time.getItemId(uri)
                    );
        }
    }
}
