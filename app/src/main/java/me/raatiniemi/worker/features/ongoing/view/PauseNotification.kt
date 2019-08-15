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

package me.raatiniemi.worker.features.ongoing.view

import android.app.Notification
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.ongoing.service.ClockOutService
import me.raatiniemi.worker.features.ongoing.service.PauseService
import java.util.*

/**
 * Notification for pausing or clocking out an active project.
 */
internal class PauseNotification private constructor(
    context: Context,
    project: Project,
    registeredTime: Long,
    useChronometer: Boolean
) : OngoingNotification(context, project, isOngoing = true) {
    private val textForPauseAction: String by lazy {
        getStringWithResourceId(R.string.ongoing_notification_action_pause)
    }
    private val textForClockOutAction: String by lazy {
        getStringWithResourceId(R.string.ongoing_notification_action_clock_out)
    }

    @DrawableRes
    override val smallIcon: Int = R.drawable.ic_pause_notification

    override val shouldUseChronometer = useChronometer

    override val whenForChronometer = registeredTime.let { Date().time - it }

    private fun buildPauseAction(): NotificationCompat.Action {
        val intent = buildIntentWithService(PauseService::class.java)

        return NotificationCompat.Action(
            PAUSE_ICON,
            textForPauseAction,
            buildPendingIntentWithService(intent)
        )
    }

    private fun buildClockOutAction(): NotificationCompat.Action {
        val intent = buildIntentWithService(ClockOutService::class.java)

        return NotificationCompat.Action(
            CLOCK_OUT_ICON,
            textForClockOutAction,
            buildPendingIntentWithService(intent)
        )
    }

    override fun build(): Notification {
        return buildWithActions(buildPauseAction(), buildClockOutAction())
    }

    companion object {
        private const val PAUSE_ICON = 0
        private const val CLOCK_OUT_ICON = 0

        fun build(
            context: Context,
            project: Project,
            registeredTime: Long,
            useChronometer: Boolean
        ): Notification {
            return PauseNotification(context, project, registeredTime, useChronometer)
                .run { build() }
        }
    }
}
