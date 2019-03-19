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

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class FirebaseUsageAnalytics(private val firebaseAnalytics: FirebaseAnalytics) : UsageAnalytics {
    private var lastScreen: String? = null

    @MainThread
    override fun setCurrentScreen(fragment: Fragment) {
        // We need to check that the current screen is not the same as the previous screen since
        // that would cause a warning to be sent to the log.
        //
        // Also, the simple name needs to be between 1 and 36 characters, due to a limitation from
        // the `FirebaseAnalytics.setCurrentScreen` method.
        val simpleName = fragment.javaClass.simpleName.substring(0, 35)
        if (simpleName == lastScreen) {
            return
        }
        lastScreen = simpleName

        try {
            firebaseAnalytics.setCurrentScreen(fragment.requireActivity(), simpleName, simpleName)
        } catch (e: IllegalStateException) {
            Timber.w(e, "Unable to fetch activity from fragment: $simpleName")
        }
    }
}
