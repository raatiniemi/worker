package me.raatiniemi.worker.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import me.raatiniemi.worker.provider.WorkerContract.Tables;
import me.raatiniemi.worker.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.util.SelectionBuilder;

public class WorkerProvider extends ContentProvider {
    private static final int PROJECTS = 100;

    private static final int PROJECTS_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WorkerDatabase mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WorkerContract.AUTHORITY;

        matcher.addURI(authority, "projects", PROJECTS);
        matcher.addURI(authority, "projects/#", PROJECTS_ID);

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
                return ProjectContract.CONTENT_TYPE;
            case PROJECTS_ID:
                return ProjectContract.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return buildSelection(uri)
            .where(selection, selectionArgs)
            .query(mOpenHelper.getReadableDatabase(), projection, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                final long projectId = db.insertOrThrow(Tables.PROJECT, null, values);
                return ProjectContract.buildUri(String.valueOf(projectId));
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
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
                        ProjectContract.ID + "=?",
                        ProjectContract.getId(uri)
                    );
                break;
        }

        return builder;
    }
}
