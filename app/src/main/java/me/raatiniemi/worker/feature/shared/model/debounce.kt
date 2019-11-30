/*
 * Copyright (C) 2019 Tobias Raatiniemi
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
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import kotlinx.coroutines.*
import me.raatiniemi.worker.domain.time.Milliseconds

/**
 * Debounce [LiveData] values and emit new values using a suspended function.
 *
 * @param scope Coroutine scope used for emitting new [LiveData] values.
 * @param source Source for [LiveData] values.
 * @param duration Duration used for debouncing values.
 * @param function Suspending function used for transforming new value.
 *
 * @return [LiveData] which emits new values.
 */
internal fun <T, R> debounceSuspend(
    scope: CoroutineScope,
    source: LiveData<T>,
    duration: Milliseconds = Milliseconds(
        250
    ),
    function: suspend (T) -> R
): LiveData<R> {
    return debounce(
        scope,
        source,
        duration.value
    )
        .switchMap(
            emitLiveDataValue(
                scope,
                function
            )
        )
}

/**
 * Emit values, produced by suspending function, using a [LiveData].
 *
 * @param scope Coroutine scope used for emitting new [LiveData] values.
 * @param function Suspending function used for transforming new value.
 *
 * @return [LiveData] which emits new values.
 */
private fun <R, T> emitLiveDataValue(
    scope: CoroutineScope,
    function: suspend (T) -> R
): (T) -> LiveData<R> {
    return { value ->
        liveData(scope.coroutineContext) {
            emit(function(value))
        }
    }
}

/**
 * Debounce emitted values from [LiveData] source for duration in milliseconds.
 *
 * @param scope Coroutine scope used for debouncing values.
 * @param source [LiveData] source from which to debounce emitted values.
 * @param duration Duration in milliseconds for which to debounce values.
 */
private fun <T> debounce(
    scope: CoroutineScope,
    source: LiveData<T>,
    duration: Long = 250
): LiveData<T> {
    var job: Job? = null
    val mediator = MediatorLiveData<T>()
    mediator.addSource(source) { value ->
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            delay(duration)

            mediator += value
        }
    }

    return mediator
}
