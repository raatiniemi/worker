package me.raatiniemi.worker.model.domain.time;

import android.content.ContentValues;
import android.database.Cursor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.exception.domain.ClockOutBeforeClockInException;
import me.raatiniemi.worker.model.domain.project.Project;
import me.raatiniemi.worker.model.domain.project.ProjectMapper;
import me.raatiniemi.worker.provider.WorkerContract;
import me.raatiniemi.worker.provider.WorkerContract.TimeColumns;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TimeMapperTest {
    @Test
    public void map_CursorToProject_True() {
        Cursor cursor = mock(Cursor.class);

        when(cursor.getColumnIndex(TimeColumns._ID)).thenReturn(0);
        when(cursor.getLong(0)).thenReturn(1L);

        when(cursor.getColumnIndex(TimeColumns.PROJECT_ID)).thenReturn(1);
        when(cursor.getLong(1)).thenReturn(2L);

        when(cursor.getColumnIndex(TimeColumns.START)).thenReturn(2);
        when(cursor.getLong(2)).thenReturn(3L);

        when(cursor.getColumnIndex(TimeColumns.STOP)).thenReturn(3);
        when(cursor.getLong(3)).thenReturn(4L);

        when(cursor.getColumnIndex(TimeColumns.REGISTERED)).thenReturn(4);
        when(cursor.getLong(4)).thenReturn(1L);

        Time time = TimeMapper.map(cursor);

        assertNotNull(time);
        assertEquals(Long.valueOf(1L), time.getId());
        assertEquals(Long.valueOf(2L), time.getProjectId());
        assertEquals(Long.valueOf(3L), time.getStart());
        assertEquals(Long.valueOf(4L), time.getStop());
        assertEquals(Long.valueOf(1L), time.getRegistered());
    }

    @Test
    public void map_TimeToContentValues_True() throws ClockOutBeforeClockInException {
        Time time = new Time(1L, 2L, 3L, 4L);
        time.setRegistered(1L);

        ContentValues contentValues = TimeMapper.map(time);

        assertNull(contentValues.get(TimeColumns._ID));
        assertEquals(Long.valueOf(2L), contentValues.getAsLong(TimeColumns.PROJECT_ID));
        assertEquals(Long.valueOf(3L), contentValues.getAsLong(TimeColumns.START));
        assertEquals(Long.valueOf(4L), contentValues.getAsLong(TimeColumns.STOP));
        assertEquals(Long.valueOf(1L), contentValues.getAsLong(TimeColumns.REGISTERED));
    }
}
