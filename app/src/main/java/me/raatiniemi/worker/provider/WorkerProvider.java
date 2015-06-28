package me.raatiniemi.worker.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import me.raatiniemi.worker.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.provider.WorkerContract.TimeContract;
import me.raatiniemi.worker.provider.WorkerContract.Tables;
import me.raatiniemi.worker.util.SelectionBuilder;

public class WorkerProvider extends ContentProvider {
    private static final int PROJECTS = 100;

    private static final int PROJECTS_ID = 101;

    private static final int PROJECTS_TIME = 102;

    private static final int PROJECTS_TIMESHEET = 103;

    private static final int TIME = 200;

    private static final int TIME_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WorkerDatabase mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WorkerContract.AUTHORITY;

        matcher.addURI(authority, "projects", PROJECTS);
        matcher.addURI(authority, "projects/#", PROJECTS_ID);
        matcher.addURI(authority, "projects/#/time", PROJECTS_TIME);
        matcher.addURI(authority, "projects/#/timesheet", PROJECTS_TIMESHEET);

        matcher.addURI(authority, "time", TIME);
        matcher.addURI(authority, "time/#", TIME_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WorkerDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                return ProjectContract.STREAM_TYPE;
            case PROJECTS_ID:
                return ProjectContract.ITEM_TYPE;
            case PROJECTS_TIME:
                return TimeContract.STREAM_TYPE;
            case TIME:
                return TimeContract.STREAM_TYPE;
            case TIME_ID:
                return TimeContract.ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Build the limit section of the query, with the offset.
        // TODO: Add proper validation and additional controls.
        // TODO: Simplify the process of retrieving offset and limit.
        String limit = null;
        if (null != uri.getQueryParameter(WorkerContract.QUERY_PARAMETER_LIMIT)) {
            if (null != uri.getQueryParameter(WorkerContract.QUERY_PARAMETER_OFFSET)) {
                limit = uri.getQueryParameter(WorkerContract.QUERY_PARAMETER_OFFSET) + ",";
            }
            limit = limit + uri.getQueryParameter(WorkerContract.QUERY_PARAMETER_LIMIT);
        }

        return buildSelection(uri)
            .where(selection, selectionArgs)
            .query(mOpenHelper.getReadableDatabase(), projection, sortOrder, limit);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final long id;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                id = db.insertOrThrow(Tables.PROJECT, null, values);
                uri = ProjectContract.getItemUri(String.valueOf(id));
                break;
            case TIME:
                id = db.insertOrThrow(Tables.TIME, null, values);
                uri = TimeContract.getItemUri(String.valueOf(id));
                break;
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }

        return uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return buildSelection(uri)
            .where(selection, selectionArgs)
            .update(mOpenHelper.getWritableDatabase(), values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return buildSelection(uri)
            .where(selection, selectionArgs)
            .delete(mOpenHelper.getWritableDatabase());
    }

    /**
     * Build the selection based on the URI.
     *
     * @param uri URI for building the selection.
     * @return Selection ready to be queried.
     */
    private SelectionBuilder buildSelection(Uri uri) {
        SelectionBuilder builder = new SelectionBuilder();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                builder.table(Tables.PROJECT);
                break;
            case PROJECTS_ID:
                builder.table(Tables.PROJECT)
                    .where(
                        ProjectContract._ID + "=?",
                        ProjectContract.getItemId(uri)
                    );
                break;
            case PROJECTS_TIME:
                builder.table(Tables.TIME)
                    .where(
                        TimeContract.PROJECT_ID + "=?",
                        ProjectContract.getItemId(uri)
                    );
                break;
            case PROJECTS_TIMESHEET:
                builder.table(Tables.TIME)
                    .where(
                        TimeContract.PROJECT_ID + "=?",
                        ProjectContract.getItemId(uri)
                    )
                    .groupBy(ProjectContract.GROUP_BY_TIMESHEET);
                break;
            case TIME_ID:
                builder.table(Tables.TIME)
                    .where(
                        TimeContract._ID + "=?",
                        TimeContract.getItemId(uri)
                    );
                break;
        }

        return builder;
    }
}
