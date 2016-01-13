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

package me.raatiniemi.worker.data.repository.strategy;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.mapper.EntityMapper;

/**
 * Base for strategies using a content resolver as data source.
 *
 * @param <M> Type reference for the entity mapper used.
 */
class ContentResolverStrategy<M extends EntityMapper> {
    /**
     * Content resolver used with the strategy.
     */
    private final ContentResolver mContentResolver;

    /**
     * Entity mapper used with the strategy.
     */
    private final M mEntityMapper;

    /**
     * Constructor.
     *
     * @param contentResolver Content resolver used with the strategy.
     * @param entityMapper Entity mapper used with the strategy.
     */
    ContentResolverStrategy(@NonNull ContentResolver contentResolver, @NonNull M entityMapper) {
        mContentResolver = contentResolver;
        mEntityMapper = entityMapper;
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
     * Get the entity mapper.
     *
     * @return Entity mapper.
     */
    @NonNull
    protected M getEntityMapper() {
        return mEntityMapper;
    }
}
