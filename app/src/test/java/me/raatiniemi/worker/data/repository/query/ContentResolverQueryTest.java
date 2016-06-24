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

package me.raatiniemi.worker.data.repository.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.repository.query.Criteria;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ContentResolverQueryTest {
    private final String mMessage;
    private final String mExpectedSelection;
    private final String[] mExpectedSelectionArgs;
    private final Criteria mCriteria;

    public ContentResolverQueryTest(
            String message,
            String expectedSelection,
            String[] expectedSelectionArgs,
            Criteria criteria
    ) {
        mMessage = message;
        mExpectedSelection = expectedSelection;
        mExpectedSelectionArgs = expectedSelectionArgs;
        mCriteria = criteria;
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                {
                        "null criteria",
                        null,
                        null,
                        null
                },
                {
                        "equalTo criteria",
                        "foo=? COLLATE NOCASE",
                        new String[]{"bar"},
                        Criteria.equalTo("foo", "bar")
                }
        });
    }

    @Test
    public void from() {
        if (haveValidCriteria()) {
            assertContentResolverQuery();
            return;
        }

        assertNullCriteria();
    }

    private boolean haveValidCriteria() {
        return null != mCriteria;
    }

    private void assertContentResolverQuery() {
        ContentResolverQuery actual = ContentResolverQuery.from(mCriteria);

        assertEquals(mMessage, mExpectedSelection, actual.getSelection());

        String[] actualSelectionArgs = actual.getSelectionArgs();
        assertTrue(mMessage, mExpectedSelectionArgs.length == actualSelectionArgs.length);

        for (int i = 0, l = mExpectedSelectionArgs.length; i < l; i++) {
            assertEquals(mMessage, mExpectedSelectionArgs[i], actualSelectionArgs[i]);
        }
    }

    private void assertNullCriteria() {
        ContentResolverQuery actual = ContentResolverQuery.from(mCriteria);

        assertNull(mMessage, actual.getSelection());
        assertTrue(mMessage, 0 == actual.getSelectionArgs().length);
    }
}
