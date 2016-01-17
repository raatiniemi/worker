/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.data.mapper;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.domain.mapper.EntityMapper;

/**
 * Interface for entity to {@link ContentValues} transformation.
 *
 * @param <T> Type reference for entity to transform.
 */
public interface ContentValuesMapper<T> extends EntityMapper<ContentValues, T> {
    /**
     * Perform transformation to {@link ContentValues} from an entity.
     *
     * @param entity Entity to transform.
     * @return Transformed content values.
     */
    @Override
    @NonNull
    ContentValues transform(@NonNull T entity);
}
