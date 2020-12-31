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

package me.raatiniemi.worker.monitor.analytics

import androidx.annotation.MainThread
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import me.raatiniemi.worker.util.runOnMainThread
import me.raatiniemi.worker.util.truncate
import timber.log.Timber

internal class FirebaseUsageAnalytics(
    private val analytics: FirebaseAnalytics
) : UsageAnalytics {
    private var lastScreenName: ScreenName = ScreenName.Empty

    @MainThread
    override fun setCurrentScreen(fragment: Fragment) {
        // We need to check that the current screen is not the same as the previous
        // screen since that would cause a warning to be sent to the log.
        val screenName = screenName(fragment)
        if (equal(screenName, lastScreenName)) {
            return
        }

        lastScreenName = screenName

        Timber.v("Set current screen to: $screenName")
        analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to name(screenName),
                FirebaseAnalytics.Param.SCREEN_CLASS to fragment.javaClass.name
            )
        )
    }

    override fun log(event: Event) = runOnMainThread {
        with(event) {
            analytics.logEvent(
                truncate(name.value, 40),
                bundleOf(
                    *parameters.map { it.key to it.value }
                        .toTypedArray()
                )
            )
        }
    }
}
