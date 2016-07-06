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

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CriteriaTest {
    private String mMessage;
    private String mExpected;
    private Criteria mCriteria;

    public CriteriaTest(
            String message,
            String expected,
            Criteria criteria
    ) {
        mMessage = message;
        mExpected = expected;
        mCriteria = criteria;
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Equal to",
                                "foo=bar",
                                Criteria.equalTo("foo", "bar")
                        }
                }
        );
    }

    @Test
    public void validate() {
        assertEquals(mMessage, mExpected, mCriteria.toString());
    }
}
