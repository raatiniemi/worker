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

import android.database.Cursor;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.mapper.EntityMapper;

/**
 * Interface for transforming {@link Cursor} to domain entity.
 *
 * @param <T> Type reference from the domain entity.
 */
public interface CursorMapper<T> extends EntityMapper<T, Cursor> {
    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    T transform(@NonNull Cursor cursor) throws DomainException;
}
