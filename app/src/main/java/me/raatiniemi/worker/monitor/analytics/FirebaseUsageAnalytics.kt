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
import me.raatiniemi.worker.util.truncate
import timber.log.Timber

class FirebaseUsageAnalytics(private val firebaseAnalytics: FirebaseAnalytics) : UsageAnalytics {
    private var lastScreenName: String? = null

    @MainThread
    override fun setCurrentScreen(fragment: Fragment) {
        with(fragment) {
            // We need to check that the current screen is not the same as the previous
            // screen since that would cause a warning to be sent to the log.
            screenName.takeUnless { it == lastScreenName }
                    ?.also { newScreenName ->
                        Timber.v("Set current screen to: $newScreenName")
                        lastScreenName = newScreenName

                        try {
                            firebaseAnalytics.setCurrentScreen(requireActivity(), newScreenName, newScreenName)
                        } catch (e: IllegalStateException) {
                            Timber.w(e, "Unable to set current screen to $newScreenName, no activity is available")
                        }
                    }
        }
    }

    private val Fragment.screenName: String
        get() {
            // The simple name needs to be between 1 and 36 characters, due to a limitation from
            // the `FirebaseAnalytics.setCurrentScreen` method.
            return javaClass.simpleName.truncate(36)
        }

    @MainThread
    override fun log(event: Event) {
        with(event) {
            firebaseAnalytics.logEvent(
                    name.truncate(40),
                    transformToBundle(parameters)
            )
        }
    }

    private fun transformToBundle(parameters: Map<String, String>): Bundle {
        return Bundle().apply {
            parameters.forEach { key, value ->
                putString(key, value)
            }
        }
    }
}
