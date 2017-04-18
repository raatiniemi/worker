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

import static junit.framework.Assert.assertEquals;
import static me.raatiniemi.worker.data.provider.WorkerContract.QUERY_PARAMETER_LIMIT;
import static me.raatiniemi.worker.data.provider.WorkerContract.QUERY_PARAMETER_OFFSET;

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
                PageRequest.withOffset(20)
        );

        assertEquals("20", uri.getQueryParameter(QUERY_PARAMETER_OFFSET));
        assertEquals("10", uri.getQueryParameter(QUERY_PARAMETER_LIMIT));
    }
}
