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

package me.raatiniemi.worker.monitor.logging

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t == null) {
            Crashlytics.log(message)
            return
        }

        if (shouldDiscardExceptionBasedOnPriority(priority)) {
            return
        }

        Crashlytics.setString(CRASHLYTICS_KEY_PRIORITY, readable(priority))
        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)
        Crashlytics.logException(t)
    }

    private fun readable(priority: Int): String {
        return when (priority) {
            Log.VERBOSE -> "Verbose"
            Log.DEBUG -> "Debug"
            Log.INFO -> "Information"
            Log.WARN -> "Warning"
            Log.ERROR -> "Error"
            else -> "Unknown"
        }
    }

    private fun shouldDiscardExceptionBasedOnPriority(priority: Int) =
        priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO

    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}