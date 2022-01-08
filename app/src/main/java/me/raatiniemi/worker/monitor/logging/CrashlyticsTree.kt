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

package me.raatiniemi.worker.monitor.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree(private val crashlytics: FirebaseCrashlytics) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t == null) {
            crashlytics.log(message)
            return
        }

        if (shouldDiscardExceptionBasedOnPriority(priority)) {
            return
        }

        crashlytics.setCustomKey(CRASHLYTICS_KEY_PRIORITY, readable(priority))
        crashlytics.setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)
        crashlytics.recordException(t)
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
