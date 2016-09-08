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

package me.raatiniemi.worker.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CalculatedTimeEqualsHashCodeTest {
    private String message;
    private Boolean expected;
    private CalculatedTime calculatedTime;
    private Object compareTo;

    public CalculatedTimeEqualsHashCodeTest(
            String message,
            Boolean expected,
            CalculatedTime calculatedTime,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.calculatedTime = calculatedTime;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        CalculatedTime calculatedTime = createCalculatedTime(3, 15);

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                calculatedTime,
                                calculatedTime
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                calculatedTime,
                                null
                        },
                        {
                                "Same hour and minutes",
                                Boolean.TRUE,
                                calculatedTime,
                                createCalculatedTime(3, 15)
                        },
                        {
                                "Different hour",
                                Boolean.FALSE,
                                calculatedTime,
                                createCalculatedTime(4, 15)
                        },
                        {
                                "Different minute",
                                Boolean.FALSE,
                                calculatedTime,
                                createCalculatedTime(3, 16)
                        }
                }
        );
    }

    private static CalculatedTime createCalculatedTime(int hours, int minutes) {
        return new CalculatedTime(hours, minutes);
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
        assertTrue(message, calculatedTime.equals(compareTo));

        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, calculatedTime.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, calculatedTime.equals(compareTo));

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (null == compareTo) {
            return;
        }

        assertFalse(message, calculatedTime.hashCode() == compareTo.hashCode());
    }
}
