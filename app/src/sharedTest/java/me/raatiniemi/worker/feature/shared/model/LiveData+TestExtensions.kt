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

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@MainThread
fun <T> LiveData<T>.observeNonNull(timeOutInMilliseconds: Long = 250, tests: (it: T) -> Unit) {
    val latch = CountDownLatch(1)

    observeForever {
        latch.countDown()

        tests(it)
    }

    val didObserveValue = latch.await(timeOutInMilliseconds, TimeUnit.MILLISECONDS)
    if (didObserveValue) {
        return
    }

    throw ObserveTimeOutException("No value was observed before call timed out")
}

@MainThread
fun <T> LiveData<T>.observeNoValue(timeOutInMilliseconds: Long = 250) {
    val latch = CountDownLatch(1)

    observeForever {
        latch.countDown()
    }

    val didObserveValue = latch.await(timeOutInMilliseconds, TimeUnit.MILLISECONDS)
    if (didObserveValue) {
        throw ObservedUnexpectedValueException("Observed value when no value was expected")
    }
}
