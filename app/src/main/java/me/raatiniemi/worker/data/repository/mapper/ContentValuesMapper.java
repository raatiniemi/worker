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

package me.raatiniemi.worker.data.repository.mapper;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import me.raatiniemi.worker.domain.repository.mapper.DataMapper;

/**
 * Interface for transforming domain entity to {@link ContentValues}.
 *
 * @param <T> Type reference for domain entity to transform.
 */
interface ContentValuesMapper<T> extends DataMapper<ContentValues, T> {
    /**
     * Perform the transformation.
     *
     * @param entity Domain entity to be transformed.
     * @return Transformed domain entity.
     */
    @Override
    @NonNull
    ContentValues transform(@NonNull T entity);
}
