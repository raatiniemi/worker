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

package me.raatiniemi.worker.data.entity;

import android.support.annotation.Nullable;

/**
 * Represent a database row from the time table.
 */
public class TimeEntity extends BaseEntity {
    /**
     * Id for the associated project.
     */
    private long mProjectId;

    /**
     * Timestamp, in milliseconds, for when the time interval started.
     */
    private long mStart;

    /**
     * Timestamp, in milliseconds, for when the time interval stopped.
     */
    private Long mStop;

    /**
     * Flag for whether the time interval have been registered.
     */
    private boolean mRegistered;

    /**
     * Constructor.
     *
     * @param id         Id for the time.
     * @param projectId  Id for the associated project.
     * @param start      Timestamp, in milliseconds, for when the time interval started.
     * @param stop       Timestamp, in milliseconds, for when the time interval stopped.
     * @param registered Flag for whether the time interval have been registered.
     */
    public TimeEntity(long id, long projectId, long start, @Nullable Long stop, boolean registered) {
        super(id);

        mProjectId = projectId;
        mStart = start;
        mStop = stop;
        mRegistered = registered;
    }

    /**
     * Get id for the associated project.
     *
     * @return Id for the associated project.
     */
    public long getProjectId() {
        return mProjectId;
    }

    /**
     * Get timestamp, in milliseconds, for when the time interval started.
     *
     * @return Timestamp, in milliseconds, for when the time interval started.
     */
    public long getStart() {
        return mStart;
    }

    /**
     * Get timestamp, in milliseconds, for when the time interval stopped.
     *
     * @return Timestamp, in milliseconds, for when the time interval stopped, or null if active.
     */
    @Nullable
    public Long getStop() {
        return mStop;
    }

    /**
     * Check whether time interval have been registered.
     *
     * @return True if time interval have been registered, otherwise false.
     */
    public boolean isRegistered() {
        return mRegistered;
    }
}
