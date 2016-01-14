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

package me.raatiniemi.worker.domain.mapper;

/**
 * Interface for handling entity transformation.
 * <p/>
 * The transformation interface should support both from and to entities,
 * hence the double type reference instead of a single.
 *
 * @param <T> Type reference from transformation destination.
 * @param <F> Type reference from transformation source.
 */
public interface EntityMapper<T, F> {
    /**
     * Perform the entity transformation.
     *
     * @param from Data to be transformed to another type.
     * @return Data transformed to destination type.
     */
    T transform(F from);
}
