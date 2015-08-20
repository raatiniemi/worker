package me.raatiniemi.worker.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.util.Worker;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class WorkerDatabaseTest {
    private SQLiteDatabase mDatabase;

    @Before
    public void setUp() {
        // Create an in-memory database used for running unit tests.
        mDatabase = SQLiteDatabase.openDatabase(
            ":memory:",
            null,
            SQLiteDatabase.CREATE_IF_NECESSARY
        );
    }

    @After
    public void tearDown() {
        // If needed close the in-memory database.
        if (null != mDatabase && mDatabase.isOpen()) {
            mDatabase.close();
        }
        mDatabase = null;
    }

    @Test
    public void onCreate_CreateStructure_Created() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onCreate(mDatabase);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onUpgrade_OldVersionIsLessThan1_ThrowException() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onUpgrade(mDatabase, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onUpgrade_NewVersionIsMoreThanLatestVersion_ThrowException() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        int newVersion = Worker.DATABASE_VERSION + 1;
        helper.onUpgrade(mDatabase, 1, newVersion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onUpgrade_NewVersionIsLessThanOldVersion_ThrowException() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onUpgrade(mDatabase, 2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onDowngrade_NewVersionIsLessThan1_ThrowException() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onDowngrade(mDatabase, 1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onDowngrade_OldVersionIsLessThanNewVersion_ThrowException() {
        Context context = mock(Context.class);
        WorkerDatabase helper = new WorkerDatabase(context);

        helper.onDowngrade(mDatabase, 1, 2);
    }
}
