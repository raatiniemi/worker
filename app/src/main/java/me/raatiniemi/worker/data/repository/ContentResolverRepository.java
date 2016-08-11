/*
 * Copyright (C) 2015-2016 Worker Project
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
import me.raatiniemi.worker.data.mapper.CursorMapper;

/**
 * Base for repositories using a content a content resolver as data source.
 *
 * @param <M> Type reference for the cursor mapper used.
 * @param <V> Type reference for the ContentValues mapper used.
 */
class ContentResolverRepository<M extends CursorMapper, V extends ContentValuesMapper> {
    /**
     * Content resolver used with the repository.
     */
    private final ContentResolver contentResolver;

    /**
     * Cursor mapper used with the repository.
     */
    private final M cursorMapper;

    /**
     * ContentValues mapper used with the repository.
     */
    private final V contentValuesMapper;

    /**
     * Constructor.
     *
     * @param contentResolver     Content resolver used with the repository.
     * @param cursorMapper        Cursor mapper used with the repository.
     * @param contentValuesMapper ContentValues mapper used with the repository.
     */
    ContentResolverRepository(
            @NonNull ContentResolver contentResolver,
            @NonNull M cursorMapper,
            @NonNull V contentValuesMapper
    ) {
        this.contentResolver = contentResolver;
        this.cursorMapper = cursorMapper;
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
     * Get the cursor mapper.
     *
     * @return Cursor mapper.
     */
    @NonNull
    M getCursorMapper() {
        return cursorMapper;
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
