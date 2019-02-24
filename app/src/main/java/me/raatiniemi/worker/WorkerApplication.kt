/*
 * Copyright (C) 2018 Tobias Raatiniemi
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
import android.app.NotificationManager
import android.content.Context
import me.raatiniemi.worker.data.dataModule
import me.raatiniemi.worker.data.service.ongoing.ReloadNotificationService
import me.raatiniemi.worker.features.project.projectModule
import me.raatiniemi.worker.features.projects.projectsModule
import me.raatiniemi.worker.features.settings.settingsModule
import me.raatiniemi.worker.util.Notifications
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Stores application constants.
 */
open class WorkerApplication : Application() {
    private val notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    internal open val isUnitTesting: Boolean
        get() = false

    override fun onCreate() {
        super.onCreate()

        if (!isUnitTesting) {
            startKoin(this, listOf(
                    preferenceModule,
                    dataModule,
                    projectModule,
                    projectsModule,
                    settingsModule
            ))

            registerNotificationChannel()
            ReloadNotificationService.startServiceWithContext(this)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun registerNotificationChannel() {
        try {
            val notificationManager = notificationManager
            Notifications.createChannel(
                    notificationManager,
                    Notifications.ongoingChannel(resources)
            )
        } catch (e: ClassCastException) {
            Timber.e(e)
        } catch (e: NullPointerException) {
            Timber.e(e)
        }
    }

    companion object {
        /**
         * Package for the application.
         */
        const val PACKAGE = "me.raatiniemi.worker"

        /**
         * Name of the application database.
         */
        const val DATABASE_NAME = "worker"

        const val NOTIFICATION_ON_GOING_ID = 3

        /**
         * Prefix for backup directories.
         */
        const val STORAGE_BACKUP_DIRECTORY_PREFIX = "backup-"

        /**
         * Pattern for the backup directories.
         */
        const val STORAGE_BACKUP_DIRECTORY_PATTERN = WorkerApplication.STORAGE_BACKUP_DIRECTORY_PREFIX + "(\\d+)"

        /**
         * Intent action for restarting the application.
         */
        const val INTENT_ACTION_RESTART = "action_restart"
    }
}
