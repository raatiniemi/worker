package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.provider.WorkerContract.TimeColumns;
import me.raatiniemi.worker.provider.WorkerContract.TimeContract;

public class TimeMapper {
    private Context mContext;

    public TimeMapper(Context context) {
        super();

        mContext = context;
    }

    public boolean remove(Time time) {
        return 0 < mContext.getContentResolver().delete(
            TimeContract.getItemUri(String.valueOf(time.getId())),
            null,
            null
        );
    }

    public static Time map(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(TimeColumns._ID));
        long projectId = cursor.getLong(cursor.getColumnIndex(TimeColumns.PROJECT_ID));
        long start = cursor.getLong(cursor.getColumnIndex(TimeColumns.START));
        long stop = cursor.getLong(cursor.getColumnIndex(TimeColumns.STOP));

        try {
            return new Time(id, projectId, start, stop);
        } catch (DomainException e) {
            // TODO: Handle DomainException properly.
            return null;
        }
    }

    public static ContentValues map(Time time) {
        ContentValues values = new ContentValues();
        values.put(TimeColumns.START, time.getStart());
        values.put(TimeColumns.STOP, time.getStop());
        values.put(TimeColumns.PROJECT_ID, time.getProjectId());

        return values;
    }
}
