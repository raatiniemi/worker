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

package me.raatiniemi.worker.data.repository;

import android.content.ContentResolver;

import androidx.annotation.NonNull;

/**
 * Base for repositories using a content a content resolver as data source.
 */
abstract class ContentResolverRepository {
    /**
     * Content resolver used with the repository.
     */
    private final ContentResolver contentResolver;

    /**
     * Constructor.
     *
     * @param contentResolver Content resolver used with the repository.
     */
    ContentResolverRepository(@NonNull ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    /**
     * Get the content resolver.
     *
     * @return Content resolver.
     */
    @NonNull
    ContentResolver getContentResolver() {
        return contentResolver;
    }
}
