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

package me.raatiniemi.worker.feature.shared.view

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import me.raatiniemi.worker.domain.time.minutes
import java.util.*
import kotlin.concurrent.schedule

internal class RefreshTimeIntervalLifecycleObserver(
    private val refreshTimeIntervalInMilliseconds: Long = 1.minutes,
    private val refresh: () -> Unit
) : LifecycleObserver {
    private var refreshTimer: Timer? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        startRefreshTimer()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        cancelRefreshTimer()
    }

    private fun startRefreshTimer() {
        cancelRefreshTimer()

        refreshTimer = Timer().also { timer ->
            timer.schedule(Date(), refreshTimeIntervalInMilliseconds) {
                refresh()
            }
        }
    }

    private fun cancelRefreshTimer() {
        refreshTimer?.cancel()
        refreshTimer = null
    }
}
