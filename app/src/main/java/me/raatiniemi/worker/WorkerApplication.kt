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

package me.raatiniemi.worker

import android.app.Application
import me.raatiniemi.worker.koin.defaultKoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

open class WorkerApplication : Application() {
    internal open val isUnitTesting: Boolean
        get() = false

    override fun onCreate() {
        super.onCreate()

        if (!isUnitTesting) {
            startKoin {
                androidContext(applicationContext)
                modules(defaultKoinModules)
            }

            configureLogging()
        }
    }

    private fun configureLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            return
        }
    }
}
