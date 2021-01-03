/*
 * Copyright (C) 2021 Tobias Raatiniemi
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
import androidx.lifecycle.MediatorLiveData
import timber.log.Timber

internal fun <T, R> combineLatest(lhs: LiveData<T>, rhs: LiveData<R>): LiveData<Pair<T, R>> {
    return MediatorLiveData<Pair<T, R>>().apply {
        var lhsValue: T? = lhs.value
        var rhsValue: R? = rhs.value

        fun checkForUpdate() {
            val first = lhsValue ?: return
            val second = rhsValue ?: return

            value = Pair(first, second)
        }

        addSource(lhs) {
            lhsValue = it

            checkForUpdate()
        }
        addSource(rhs) {
            rhsValue = it

            checkForUpdate()
        }
    }
}

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

/**
 * Consume non-null values from a [LiveData] source with support for suspending calls.
 *
 * @param source Source from which to consume values.
 * @param consumer Consumer which consumes values emitted from source.
 */
internal suspend fun <T : Any> consumeSuspending(
    source: LiveData<T>,
    consumer: suspend (T) -> Unit
) {
    try {
        consumer(requireNotNull(source.value))
    } catch (e: IllegalArgumentException) {
        Timber.w(e, "No value is available for consumption")
    }
}
