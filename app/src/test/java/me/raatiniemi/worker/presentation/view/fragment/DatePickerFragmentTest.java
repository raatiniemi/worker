package me.raatiniemi.worker.presentation.view.fragment;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import me.raatiniemi.worker.BuildConfig;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class DatePickerFragmentTest {
    private DatePickerFragment fragment;
    private Calendar today;
    private Calendar nextYear;

    @Before
    public void setUp() throws Exception {
        fragment = DatePickerFragment.newInstance(
                (view, year, month, dayOfMonth) -> {
                }
        );

        today = Calendar.getInstance();
        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
    }

    @Test
    public void setMinDate_withValidDate() {
        fragment.setMinDate(today);
    }

    @Test(expected = RuntimeException.class)
    public void setMinDate_afterMaxDate() {
        fragment.setMaxDate(today);
        fragment.setMinDate(nextYear);
    }

    @Test
    public void setMaxDate_withValidDate() {
        fragment.setMaxDate(nextYear);
    }

    @Test(expected = RuntimeException.class)
    public void setMaxDate_beforeMinDate() {
        fragment.setMinDate(nextYear);
        fragment.setMaxDate(today);
    }
}
