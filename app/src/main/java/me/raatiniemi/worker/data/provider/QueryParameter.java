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

import androidx.annotation.NonNull;
import me.raatiniemi.worker.domain.repository.PageRequest;
import me.raatiniemi.worker.util.Optional;

import static java.util.Objects.requireNonNull;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public final class QueryParameter {
    static final String OFFSET = "offset";
    static final String LIMIT = "limit";

    private QueryParameter() {
    }

    public static Uri appendPageRequest(Uri uri, PageRequest pageRequest) {
        requireNonNull(uri, "Uri is required when appending page request");
        requireNonNull(pageRequest, "Page request is required when appending to Uri");

        return uri.buildUpon()
                .appendQueryParameter(OFFSET, String.valueOf(pageRequest.getOffset()))
                .appendQueryParameter(LIMIT, String.valueOf(pageRequest.getMaxResults()))
                .build();
    }

    @NonNull
    static Optional<PageRequest> extractPageRequestFromUri(@NonNull Uri uri) {
        String rawLimit = uri.getQueryParameter(LIMIT);
        if (isNull(rawLimit)) {
            return Optional.empty();
        }

        int limit = Integer.parseInt(rawLimit);

        String rawOffset = uri.getQueryParameter(OFFSET);
        if (isNull(rawOffset)) {
            PageRequest pageRequest = PageRequest.Companion.withMaxResults(limit);
            return Optional.of(pageRequest);
        }

        int offset = Integer.parseInt(rawOffset);

        PageRequest pageRequest = PageRequest.Companion.withOffsetAndMaxResults(offset, limit);
        return Optional.of(pageRequest);

    }
}
