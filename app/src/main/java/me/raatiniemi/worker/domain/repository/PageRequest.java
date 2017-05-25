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

package me.raatiniemi.worker.domain.repository;

public class PageRequest {
    private static final int MAX_RESULTS = 10;

    private final int offset;
    private final int maxResults;

    private PageRequest(final int offset, int maxResults) {
        this.offset = offset;
        this.maxResults = maxResults;
    }

    public static PageRequest withOffsetAndMaxResults(final int offset, final int maxResults) {
        return new PageRequest(offset, maxResults);
    }

    public static PageRequest withOffset(final int offset) {
        return PageRequest.withOffsetAndMaxResults(offset, MAX_RESULTS);
    }

    public static PageRequest withMaxResults(final int maxResults) {
        return PageRequest.withOffsetAndMaxResults(0, maxResults);
    }

    public int getOffset() {
        return offset;
    }

    public int getMaxResults() {
        return maxResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PageRequest)) {
            return false;
        }

        PageRequest that = (PageRequest) o;
        return offset == that.offset;

    }

    @Override
    public int hashCode() {
        return offset;
    }
}
