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

package me.raatiniemi.worker.features.shared.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*

internal fun <T, R> LiveData<T>.map(function: (T) -> R): LiveData<R> {
    return Transformations.map(this) { function(it) }
}

internal fun <T> LiveData<T>.debounce(
        duration: Long = 250,
        context: CoroutineScope = GlobalScope
) = MediatorLiveData<T>().also { mld ->
    var job: Job? = null

    mld.addSource(this) {
        job?.cancel()

        job = context.launch {
            delay(duration)

            mld.postValue(it)
        }
    }
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
