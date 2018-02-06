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

package me.raatiniemi.worker.data.provider;

import android.net.Uri;

import org.junit.Test;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static me.raatiniemi.worker.data.provider.QueryParameter.LIMIT;
import static me.raatiniemi.worker.data.provider.QueryParameter.OFFSET;

public class QueryParameterTest extends RobolectricTestCase {
    @Test(expected = NullPointerException.class)
    public void appendPageRequest_withNullUri() {
        QueryParameter.appendPageRequest(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void appendPageRequest_withNullPageRequest() {
        QueryParameter.appendPageRequest(Uri.EMPTY, null);
    }

    @Test
    public void appendPageRequest() {
        Uri uri = QueryParameter.appendPageRequest(
                Uri.parse("/relative-path"),
                PageRequest.Companion.withOffset(20)
        );

        assertEquals("20", uri.getQueryParameter(OFFSET));
        assertEquals("10", uri.getQueryParameter(LIMIT));
    }

    @Test
    public void extractPageRequestFromUri_withEmptyUri() {
        Optional<PageRequest> value = QueryParameter.extractPageRequestFromUri(Uri.EMPTY);

        assertFalse(value.isPresent());
    }

    @Test
    public void extractPageRequestFromUri_withoutLimit() {
        Uri uri = Uri.parse("/uri");
        Optional<PageRequest> value = QueryParameter.extractPageRequestFromUri(uri);

        assertFalse(value.isPresent());
    }

    @Test
    public void extractPageRequestFromUri_withoutOffset() {
        PageRequest pageRequest = PageRequest.Companion.withMaxResults(10);
        Uri uri = QueryParameter.appendPageRequest(Uri.parse("/uri"), pageRequest);
        Optional<PageRequest> value = QueryParameter.extractPageRequestFromUri(uri);

        assertTrue(value.isPresent());
        assertEquals(value.get(), pageRequest);
    }

    @Test
    public void extractPageRequestFromUri() {
        PageRequest pageRequest = PageRequest.Companion.withOffset(10);
        Uri uri = QueryParameter.appendPageRequest(Uri.parse("/uri"), pageRequest);
        Optional<PageRequest> value = QueryParameter.extractPageRequestFromUri(uri);

        assertTrue(value.isPresent());
        assertEquals(value.get(), pageRequest);
    }
}
