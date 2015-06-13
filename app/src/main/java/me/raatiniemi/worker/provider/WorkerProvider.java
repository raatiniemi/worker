package me.raatiniemi.worker.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import me.raatiniemi.worker.provider.WorkerContract.Projects;

public class WorkerProvider extends ContentProvider {
    private static final int PROJECTS = 100;

    private static final int PROJECTS_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WorkerDatabase mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WorkerContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "projects", PROJECTS);
        matcher.addURI(authority, "projects/*", PROJECTS_ID);

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
                return Projects.CONTENT_TYPE;
            case PROJECTS_ID:
                return Projects.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
