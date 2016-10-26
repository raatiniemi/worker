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

package me.raatiniemi.worker.presentation.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Represents an action performed with the view, if the view is available, and return the result.
 *
 * @param <V> Type of the view on which to operate.
 * @param <T> Type of the data to return as the result.
 */
@FunctionalInterface
public interface ViewFunction<V, T> {
    /**
     * Performs the action with the view, and return the result.
     *
     * @param view View on which to operate.
     * @return Data fetched from the view.
     */
    @Nullable
    T get(@NonNull V view);
}
