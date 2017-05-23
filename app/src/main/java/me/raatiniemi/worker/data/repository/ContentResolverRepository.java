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
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.mapper.ContentValuesMapper;

/**
 * Base for repositories using a content a content resolver as data source.
 *
 * @param <V> Type reference for the ContentValues mapper used.
 */
abstract class ContentResolverRepository<V extends ContentValuesMapper> {
    /**
     * Content resolver used with the repository.
     */
    private final ContentResolver contentResolver;

    /**
     * ContentValues mapper used with the repository.
     */
    private final V contentValuesMapper;

    /**
     * Constructor.
     *
     * @param contentResolver     Content resolver used with the repository.
     * @param contentValuesMapper ContentValues mapper used with the repository.
     */
    ContentResolverRepository(
            @NonNull ContentResolver contentResolver,
            @NonNull V contentValuesMapper
    ) {
        this.contentResolver = contentResolver;
        this.contentValuesMapper = contentValuesMapper;
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

    /**
     * Get the ContentValues mapper.
     *
     * @return ContentValues mapper.
     */
    V getContentValuesMapper() {
        return contentValuesMapper;
    }
}
