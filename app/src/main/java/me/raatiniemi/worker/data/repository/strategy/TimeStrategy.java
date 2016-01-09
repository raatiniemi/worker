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

package me.raatiniemi.worker.data.repository.strategy;

import android.support.annotation.NonNull;

import me.raatiniemi.worker.domain.Time;
import rx.Observable;

/**
 * Define commonalities between different time strategies.
 * <p>
 * TODO: Refactor return value for TimeStrategy.
 * When the domain layer have been migrated to handle TimeEntity, the data layer
 * should return TimeEntity instead of Time.
 * <p>
 * Should the strategy return the TimeEntity or id and the TimeRepository maps
 * to to the TimeEntity?
 */
public interface TimeStrategy {
    /**
     * Get time with id.
     *
     * @param id Id for the time.
     * @return Observable emitting time.
     */
    @NonNull
    Observable<Time> get(long id);

    /**
     * Add time.
     *
     * @param time Time to add.
     * @return Observable emitting added time.
     */
    @NonNull
    Observable<Time> add(Time time);

    /**
     * Update time.
     *
     * @param time Time to update.
     * @return Observable emitting updated time.
     */
    @NonNull
    Observable<Time> update(Time time);

    /**
     * Remove time by id.
     *
     * @param id Id of the time to remove.
     * @return Observable emitting time id.
     * TODO: When removing time, return void or something more meaningful?
     */
    @NonNull
    Observable<Long> remove(long id);
}
