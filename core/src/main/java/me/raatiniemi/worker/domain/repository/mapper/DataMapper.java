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

package me.raatiniemi.worker.domain.repository.mapper;

/**
 * Interface for transforming from domain entity to another data source.
 * <p/>
 * The interface should only be used for transforming from domain entity to
 * another data source.
 * <p/>
 * For transforming to domain entity the {@link EntityMapper} should be used.
 *
 * @param <T> Type reference for the transformation destination.
 * @param <F> Type reference for the domain entity.
 */
@FunctionalInterface
public interface DataMapper<T, F> {
    /**
     * Perform the transformation.
     *
     * @param entity Domain entity to be transformed.
     * @return Transformed domain entity.
     */
    T transform(F entity);
}
