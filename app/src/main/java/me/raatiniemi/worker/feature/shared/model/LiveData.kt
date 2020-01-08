/*
 * Copyright (C) 2018 Tobias Raatiniemi
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
import kotlinx.coroutines.*
import timber.log.Timber

/**
 * Debounce emitted values from [LiveData] source for duration in milliseconds.
 *
 * @param source [LiveData] source from which to debounce emitted values.
 * @param duration Duration in milliseconds for which to debounce values.
 */
internal fun <T> CoroutineScope.debounce(source: LiveData<T>, duration: Long = 250): LiveData<T> {
    var job: Job? = null
    val mediator = MediatorLiveData<T>()
    mediator.addSource(source) { value ->
        job?.cancel()
        job = launch(coroutineContext + Dispatchers.IO) {
            delay(duration)

            mediator += value
        }
    }

    return mediator
}

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
 * @param source Source to consume values from.
 * @param consumer Consumer of values from source.
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