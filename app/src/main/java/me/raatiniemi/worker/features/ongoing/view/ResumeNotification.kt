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
import me.raatiniemi.worker.features.ongoing.service.ResumeService
import java.util.*

/**
 * Notification for resuming an inactive project.
 */
internal class ResumeNotification private constructor(
    context: Context,
    project: Project,
    override val shouldUseChronometer: Boolean
) : OngoingNotification(context, project, isOngoing = false) {
    private val textForResumeAction: String by lazy {
        getStringWithResourceId(R.string.ongoing_notification_action_resume)
    }

    @DrawableRes
    override val smallIcon: Int = R.drawable.ic_resume_notification

    override val whenForChronometer: Long = Date().time

    private fun buildResumeAction(): NotificationCompat.Action {
        val intent = buildIntentWithService(ResumeService::class.java)

        return NotificationCompat.Action(
            RESUME_ICON,
            textForResumeAction,
            buildPendingIntentWithService(intent)
        )
    }

    override fun build(): Notification = buildWithActions(buildResumeAction())

    companion object {
        private const val RESUME_ICON = 0

        fun build(context: Context, project: Project, useChronometer: Boolean): Notification {
            return ResumeNotification(context, project, useChronometer)
                .run { build() }
        }
    }
}
