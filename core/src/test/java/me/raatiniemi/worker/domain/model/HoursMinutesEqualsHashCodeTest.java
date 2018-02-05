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

package me.raatiniemi.worker.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static me.raatiniemi.worker.util.NullUtil.isNull;

@RunWith(Parameterized.class)
public class HoursMinutesEqualsHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final HoursMinutes hoursMinutes;
    private final Object compareTo;

    public HoursMinutesEqualsHashCodeTest(
            String message,
            Boolean expected,
            HoursMinutes hoursMinutes,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.hoursMinutes = hoursMinutes;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        HoursMinutes hoursMinutes = createHoursMinutes(3, 15);

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                hoursMinutes,
                                hoursMinutes
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                hoursMinutes,
                                null
                        },
                        {
                                "Same hour and minutes",
                                Boolean.TRUE,
                                hoursMinutes,
                                createHoursMinutes(3, 15)
                        },
                        {
                                "Different hour",
                                Boolean.FALSE,
                                hoursMinutes,
                                createHoursMinutes(4, 15)
                        },
                        {
                                "Different minute",
                                Boolean.FALSE,
                                hoursMinutes,
                                createHoursMinutes(3, 16)
                        }
                }
        );
    }

    private static HoursMinutes createHoursMinutes(int hours, int minutes) {
        return new HoursMinutes(hours, minutes);
    }

    @Test
    public void equals() {
        if (shouldBeEqual()) {
            assertEqual();
            return;
        }

        assertNotEqual();
    }

    private Boolean shouldBeEqual() {
        return expected;
    }

    private void assertEqual() {
        assertTrue(message, hoursMinutes.equals(compareTo));

        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, hoursMinutes.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, hoursMinutes.equals(compareTo));

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return;
        }

        assertFalse(message, hoursMinutes.hashCode() == compareTo.hashCode());
    }
}
