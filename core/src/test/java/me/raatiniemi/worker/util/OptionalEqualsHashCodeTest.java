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

package me.raatiniemi.worker.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static me.raatiniemi.worker.util.NullUtil.isNull;

@RunWith(Parameterized.class)
public class OptionalEqualsHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final Optional<String> optional;
    private final Object compareTo;

    public OptionalEqualsHashCodeTest(
            String message,
            Boolean expected,
            Optional<String> optional,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.optional = optional;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        Optional<String> optional = Optional.of("Value");

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                optional,
                                optional
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                optional,
                                null
                        },
                        {
                                "With same value",
                                Boolean.TRUE,
                                optional,
                                Optional.of("Value")
                        },
                        {
                                "With different value",
                                Boolean.FALSE,
                                optional,
                                Optional.of("Different value")
                        },
                        {
                                "With different data type",
                                Boolean.FALSE,
                                optional,
                                Optional.of(1L)
                        }
                }
        );
    }

    @Test
    public void equals() {
        if (shouldBeEqual()) {
            assertEqual();
            return;
        }

        assertNotEqual();
    }

    public Boolean shouldBeEqual() {
        return expected;
    }

    public void assertEqual() {
        assertTrue(message, optional.equals(compareTo));

        validateHashCodeWhenEqual();
    }

    public void validateHashCodeWhenEqual() {
        assertTrue(message, optional.hashCode() == compareTo.hashCode());
    }

    public void assertNotEqual() {
        assertFalse(message, optional.equals(compareTo));

        validateHashCodeWhenNotEqual();
    }

    public void validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return;
        }

        assertFalse(message, optional.hashCode() == compareTo.hashCode());
    }
}
