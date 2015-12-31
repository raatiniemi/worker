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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Represent a database row from the project table.
 */
public class ProjectEntity extends BaseEntity {
    /**
     * Name of the project.
     */
    private String mName;

    /**
     * Description for the project.
     */
    private String mDescription;

    /**
     * Flag for whether the project have been archived.
     */
    private boolean mArchived;

    /**
     * Constructor.
     *
     * @param id          Id for the project.
     * @param name        Name of the project.
     * @param description Description for the project.
     * @param archived    Flag for whether the project have been archived.
     */
    public ProjectEntity(
            @NonNull Long id,
            @NonNull String name,
            @Nullable String description,
            boolean archived
    ) {
        super(id);

        mName = name;
        mDescription = description;
        mArchived = archived;
    }

    /**
     * Get the name of the project.
     *
     * @return Name of the project.
     */
    @NonNull
    public String getName() {
        return mName;
    }

    /**
     * Get the description for the project.
     *
     * @return Description for the project.
     */
    @Nullable
    public String getDescription() {
        return mDescription;
    }

    /**
     * Check whether the project have been archived.
     *
     * @return True if the project is archived, otherwise false.
     */
    public boolean isArchived() {
        return mArchived;
    }
}
