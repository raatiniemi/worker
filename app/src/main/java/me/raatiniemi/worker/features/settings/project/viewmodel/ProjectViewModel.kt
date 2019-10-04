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

package me.raatiniemi.worker.features.settings.project.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore

class ProjectViewModel(private val keyValueStore: KeyValueStore) : ViewModel() {
    var ongoingNotificationEnabled: Boolean
        @MainThread
        get() = keyValueStore.bool(AppKeys.ONGOING_NOTIFICATION_ENABLED, true)
        @MainThread
        set(value) {
            keyValueStore.set(AppKeys.ONGOING_NOTIFICATION_ENABLED, value)
        }

    var ongoingNotificationChronometerEnabled: Boolean
        @MainThread
        get() {
            return if (ongoingNotificationEnabled) {
                keyValueStore.bool(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, true)
            } else {
                false
            }
        }
        @MainThread
        set(value) {
            keyValueStore.set(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, value)
        }
}
