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

package me.raatiniemi.worker.presentation.project.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static me.raatiniemi.worker.util.NullUtil.isNull;

@RunWith(Parameterized.class)
public class TimesheetAdapterResultTest {
    private final String message;
    private final Boolean expected;
    private final TimesheetAdapterResult result;
    private final Object compareTo;

    public TimesheetAdapterResultTest(
            String message,
            Boolean expected,
            TimesheetAdapterResult result,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.result = result;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        Time time = TimeFactory.builder().id(1L).build();
        TimesheetItem item = new TimesheetItem(time);
        TimesheetAdapterResult result = TimesheetAdapterResult.build(0, 0, item);

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                result,
                                result
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                result,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                result,
                                ""
                        },
                        {
                                "With different group position",
                                Boolean.FALSE,
                                result,
                                TimesheetAdapterResult.build(1, 0, new TimesheetItem(time))
                        },
                        {
                                "With different child position",
                                Boolean.FALSE,
                                result,
                                TimesheetAdapterResult.build(0, 1, new TimesheetItem(time))
                        },
                        {
                                "With different time object",
                                Boolean.FALSE,
                                result,
                                TimesheetAdapterResult.build(
                                        0,
                                        0,
                                        new TimesheetItem(
                                                TimeFactory.builder()
                                                        .id(2L)
                                                        .build()
                                        )
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
        return expected;
    }

    private void assertEqual() {
        assertTrue(message, result.equals(compareTo));
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, result.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, result.equals(compareTo));
    }

    private void validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return;
        }

        assertFalse(message, result.hashCode() == compareTo.hashCode());
    }
}
