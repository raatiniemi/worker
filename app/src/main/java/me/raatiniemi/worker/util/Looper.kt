/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.util

import android.os.Handler
import android.os.Looper

// If the `Looper.getMainLooper()` return `null` it means that we are most
// likely running tests, and therefor should assume always that we are not
// running on the main thread since we should use `InstantTaskExecutorRule`
// and `runBlocking` to simulate main thread behavior.
internal val isMainThread: Boolean
    get() {
        return try {
            Looper.getMainLooper()?.isCurrentThread ?: false
        } catch (e: NoClassDefFoundError) {
            false
        }
    }

internal fun runOnMainThread(closure: () -> Unit) {
    val mainLooper = Looper.getMainLooper()
    if (mainLooper == null) {
        closure()
        return
    }

    Handler(mainLooper).post(closure)
}
