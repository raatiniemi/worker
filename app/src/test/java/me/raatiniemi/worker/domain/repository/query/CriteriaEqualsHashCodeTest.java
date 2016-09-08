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

package me.raatiniemi.worker.domain.repository.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CriteriaEqualsHashCodeTest {
    private String message;
    private Boolean expected;
    private Criteria criteria;
    private Object compareTo;

    public CriteriaEqualsHashCodeTest(
            String message,
            Boolean expected,
            Criteria criteria,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.criteria = criteria;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        Criteria criteria = Criteria.equalTo("foo", "bar");

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                criteria,
                                criteria
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                criteria,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                criteria,
                                ""
                        },
                        {
                                "With different field",
                                Boolean.FALSE,
                                criteria,
                                Criteria.equalTo("baz", "bar")
                        },
                        {
                                "With different value",
                                Boolean.FALSE,
                                criteria,
                                Criteria.equalTo("foo", "baz")
                        },
                        {
                                "With different operator",
                                Boolean.FALSE,
                                criteria,
                                Criteria.lessThan("foo", "bar")
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

    private Boolean shouldBeEqual() {
        return expected;
    }

    private void assertEqual() {
        assertTrue(message, criteria.equals(compareTo));

        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, criteria.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, criteria.equals(compareTo));


        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (null == compareTo) {
            return;
        }

        assertFalse(message, criteria.hashCode() == compareTo.hashCode());
    }
}
