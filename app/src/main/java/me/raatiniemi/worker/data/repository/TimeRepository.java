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

package me.raatiniemi.worker.data.repository;

import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.repository.strategy.TimeStrategy;
import me.raatiniemi.worker.domain.model.Time;
import rx.Observable;

/**
 * Represents a unified interface for working with different data sources (strategies).
 */
public class TimeRepository {
    /**
     * Time repository strategy.
     */
    private TimeStrategy mStrategy;

    /**
     * Constructor.
     *
     * @param strategy Time repository strategy.
     */
    public TimeRepository(TimeStrategy strategy) {
        mStrategy = strategy;
    }

    /**
     * Get the time repository strategy.
     *
     * @return Time repository strategy.
     */
    protected TimeStrategy getStrategy() {
        return mStrategy;
    }

    /**
     * Get time with id.
     *
     * @param id Id for the time.
     * @return Observable emitting time.
     */
    @NonNull
    public Observable<Time> get(final long id) {
        return getStrategy().get(id);
    }

    /**
     * Add time.
     *
     * @param time Time to add.
     * @return Observable emitting added time.
     */
    @NonNull
    public Observable<Time> add(final Time time) {
        return getStrategy().add(time);
    }

    /**
     * Update time.
     *
     * @param time Time to update.
     * @return Observable emitting updated time.
     */
    @NonNull
    public Observable<Time> update(final Time time) {
        return getStrategy().update(time);
    }

    /**
     * Remove time by id.
     *
     * @param id Id of the time to remove.
     * @return Observable emitting time id.
     */
    @NonNull
    public Observable<Long> remove(final long id) {
        return getStrategy().remove(id);
    }
}
