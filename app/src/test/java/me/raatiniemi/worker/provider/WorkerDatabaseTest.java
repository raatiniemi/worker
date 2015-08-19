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
}
