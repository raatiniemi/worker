/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.model.timesheet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TimeInAdapterResultTest {
    private String mMessage;
    private Boolean mExpected;
    private TimeInAdapterResult mTimeInAdapterResult;
    private Object mCompareTo;

    public TimeInAdapterResultTest(
            String message,
            Boolean expected,
            TimeInAdapterResult timeInAdapterResult,
            Object compareTo
    ) {
        mMessage = message;
        mExpected = expected;
        mTimeInAdapterResult = timeInAdapterResult;
        mCompareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> parameters()
            throws ClockOutBeforeClockInException {
        Time time = new Time.Builder(0)
                .build();

        TimeInAdapterResult timeInAdapterResult = new TimeInAdapterResult(0, 0, time);

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                timeInAdapterResult,
                                timeInAdapterResult
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                timeInAdapterResult,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                timeInAdapterResult,
                                ""
                        },
                        {
                                "With different group position",
                                Boolean.FALSE,
                                timeInAdapterResult,
                                new TimeInAdapterResult(1, 0, time)
                        },
                        {
                                "With different child position",
                                Boolean.FALSE,
                                timeInAdapterResult,
                                new TimeInAdapterResult(0, 1, time)
                        },
                        {
                                "With different time object",
                                Boolean.TRUE,
                                timeInAdapterResult,
                                new TimeInAdapterResult(
                                        0,
                                        0,
                                        new Time.Builder(0)
                                                .build()
                                )
                        }
                }
        );
    }

    @Test
    public void equals() {
        if (shouldBeEqual()) {
            assertEqual();
            validateHashCodeWhenEqual();
            return;
        }

        assertNotEqual();
        validateHashCodeWhenNotEqual();
    }

    private Boolean shouldBeEqual() {
        return mExpected;
    }

    private void assertEqual() {
        assertTrue(mMessage, mTimeInAdapterResult.equals(mCompareTo));
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(mMessage, mTimeInAdapterResult.hashCode() == mCompareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(mMessage, mTimeInAdapterResult.equals(mCompareTo));
    }

    private void validateHashCodeWhenNotEqual() {
        if (null == mCompareTo) {
            return;
        }

        assertFalse(mMessage, mTimeInAdapterResult.hashCode() == mCompareTo.hashCode());
    }
}
