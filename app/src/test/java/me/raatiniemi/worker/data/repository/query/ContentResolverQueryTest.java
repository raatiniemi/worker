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
import static me.raatiniemi.worker.util.NullUtil.nonNull;

@RunWith(Parameterized.class)
public class ContentResolverQueryTest {
    private final String message;
    private final String expectedSelection;
    private final String[] expectedSelectionArgs;
    private final Criteria criteria;

    public ContentResolverQueryTest(
            String message,
            String expectedSelection,
            String[] expectedSelectionArgs,
            Criteria criteria
    ) {
        this.message = message;
        this.expectedSelection = expectedSelection;
        this.expectedSelectionArgs = expectedSelectionArgs;
        this.criteria = criteria;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
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
        return nonNull(criteria);
    }

    private void assertContentResolverQuery() {
        ContentResolverQuery actual = ContentResolverQuery.from(criteria);

        assertEquals(message, expectedSelection, actual.getSelection());

        String[] actualSelectionArgs = actual.getSelectionArgs();
        assertTrue(message, expectedSelectionArgs.length == actualSelectionArgs.length);

        for (int i = 0, l = expectedSelectionArgs.length; i < l; i++) {
            assertEquals(message, expectedSelectionArgs[i], actualSelectionArgs[i]);
        }
    }

    private void assertNullCriteria() {
        ContentResolverQuery actual = ContentResolverQuery.from(criteria);

        assertNull(message, actual.getSelection());
        assertTrue(message, 0 == actual.getSelectionArgs().length);
    }
}
