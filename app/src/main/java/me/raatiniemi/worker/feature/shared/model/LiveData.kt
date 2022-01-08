/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.shared.model

import androidx.lifecycle.LiveData
import timber.log.Timber

/**
 * Consumes non-null values from a [LiveData] source.
 *
 * @param source Source from which to consume values.
 * @param consumer Consumer which consumes values emitted from source.
 */
internal fun <T> consume(source: LiveData<T>, consumer: (T) -> Unit) {
    try {
        val value = source.value
        check(value != null)

        consumer(value)
    } catch (e: IllegalStateException) {
        Timber.w(e, "No value is available for consumer")
    }
}
