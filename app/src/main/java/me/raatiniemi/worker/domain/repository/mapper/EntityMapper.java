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

import me.raatiniemi.worker.domain.exception.DomainException;

/**
 * Interface for transforming to domain entity from another data source.
 * <p/>
 * The interface should only be used for transforming from another data source
 * to domain entity.
 * <p/>
 * For transforming from domain entity the {@link DataMapper} should be used.
 *
 * @param <T> Type reference for the domain entity.
 * @param <F> Type reference for the transformation source.
 */
@FunctionalInterface
public interface EntityMapper<T, F> {
    /**
     * Perform the transformation.
     *
     * @param from Data to be transformed.
     * @return Transformed data.
     * @throws DomainException If data violate domain rules.
     */
    T transform(F from) throws DomainException;
}
