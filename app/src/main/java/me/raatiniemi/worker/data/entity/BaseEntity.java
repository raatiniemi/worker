/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.data.entity;

/**
 * Represent the base structure for rows from the database.
 * <p/>
 * Entities are immutable, i.e. when a change is done a new object have to be created.
 * <p/>
 * Since the `BaseEntity` only should be used from within the package, the visibility for the
 * constructor have been set to package. However, the `getId` method should be available from
 * outside the package, via sub-classes.
 */
public class BaseEntity {
    /**
     * Id for the entity.
     */
    private long mId;

    /**
     * Constructor.
     *
     * @param id Id for the entity.
     */
    BaseEntity(long id) {
        mId = id;
    }

    /**
     * Get the id for the entity.
     *
     * @return Id for the entity.
     */
    public long getId() {
        return mId;
    }
}
