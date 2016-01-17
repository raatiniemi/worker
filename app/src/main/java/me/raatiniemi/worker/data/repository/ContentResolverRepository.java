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

import me.raatiniemi.worker.data.mapper.CursorMapper;

/**
 * Base for repositories using a content a content resolver as data source.
 *
 * @param <M> Type reference for the cursor mapper used.
 */
class ContentResolverRepository<M extends CursorMapper> {
    /**
     * Content resolver used with the repository.
     */
    private final ContentResolver mContentResolver;

    /**
     * Cursor mapper used with the repository.
     */
    private final M mCursorMapper;

    /**
     * Constructor.
     *
     * @param contentResolver Content resolver used with the repository.
     * @param cursorMapper    Cursor mapper used with the repository.
     */
    ContentResolverRepository(@NonNull ContentResolver contentResolver, @NonNull M cursorMapper) {
        mContentResolver = contentResolver;
        mCursorMapper = cursorMapper;
    }

    /**
     * Get the content resolver.
     *
     * @return Content resolver.
     */
    @NonNull
    protected ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /**
     * Get the cursor mapper.
     *
     * @return Cursor mapper.
     */
    @NonNull
    protected M getCursorMapper() {
        return mCursorMapper;
    }
}
