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

package me.raatiniemi.worker.data.mapper;

import android.database.Cursor;
import android.support.annotation.NonNull;

/**
 * Handle entity transformation.
 *
 * @param <T> Type reference for the transformable entity.
 */
public interface EntityMapper<T> {
    /**
     * Perform transformation from {@link Cursor} to entity.
     *
     * @param cursor Cursor to transform.
     * @return Transformed entity.
     */
    @NonNull
    T transform(@NonNull Cursor cursor);
}
