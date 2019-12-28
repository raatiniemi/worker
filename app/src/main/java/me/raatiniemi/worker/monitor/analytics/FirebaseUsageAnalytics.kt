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

package me.raatiniemi.worker.monitor.analytics

import android.os.Bundle
import androidx.annotation.MainThread
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

        Timber.v("Set current screen to: $screenName")
        lastScreenName = screenName

        try {
            analytics.setCurrentScreen(
                fragment.requireActivity(),
                name(screenName),
                name(screenName)
            )
        } catch (e: IllegalStateException) {
            Timber.w(
                e,
                "Unable to set current screen to $screenName, no activity is available"
            )
        }
    }

    override fun log(event: Event) = runOnMainThread {
        with(event) {
            analytics.logEvent(
                name.value.truncate(40),
                transformToBundle(parameters)
            )
        }
    }

    private fun transformToBundle(parameters: List<EventParameter>): Bundle {
        return Bundle().apply {
            parameters.forEach { parameter ->
                putString(parameter.key, parameter.value)
            }
        }
    }
}
