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

package me.raatiniemi.worker.feature.shared.view

import androidx.lifecycle.*
import me.raatiniemi.worker.domain.time.minutes
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

internal class RefreshTimeIntervalLifecycleObserver(
    private val refreshTimeIntervalInMilliseconds: Long = 1.minutes,
    private val refresh: () -> Unit
) : DefaultLifecycleObserver {
    private var refreshTimer: Timer? = null

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        startRefreshTimer()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)

        cancelRefreshTimer()
    }

    private fun startRefreshTimer() {
        cancelRefreshTimer()

        refreshTimer = Timer().also { timer ->
            Timber.d("Configure refresh timer with interval of $refreshTimeIntervalInMilliseconds milliseconds")
            timer.schedule(Date(), refreshTimeIntervalInMilliseconds) {
                refresh()
            }
        }
    }

    private fun cancelRefreshTimer() {
        refreshTimer = refreshTimer.let { timer ->
            if (timer == null) {
                Timber.d("No active refresh timer is available for cancellation")
                return@let null
            }

            Timber.d("Cancelling active refresh timer")
            timer.cancel()
            timer.purge()
            null
        }
    }
}
