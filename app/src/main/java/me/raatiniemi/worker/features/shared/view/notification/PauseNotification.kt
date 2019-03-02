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

package me.raatiniemi.worker.features.shared.view.notification

import android.app.Notification
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import me.raatiniemi.worker.R
import me.raatiniemi.worker.data.Repositories
import me.raatiniemi.worker.data.service.ongoing.ClockOutService
import me.raatiniemi.worker.data.service.ongoing.PauseService
import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import timber.log.Timber
import java.util.*

/**
 * Notification for pausing or clocking out an active project.
 */
internal class PauseNotification private constructor(
        context: Context,
        project: Project,
        private var useChronometer: Boolean
) : OngoingNotification(context, project) {
    private val repositories = Repositories()
    private val repository = repositories.timeInterval
    private var registeredTime: Long = 0

    private val activeTimeIntervalForProject: TimeInterval?
        get() = repository.findActiveByProjectId(project.id)

    private val textForPauseAction: String by lazy {
        getStringWithResourceId(R.string.notification_pause_action_pause)
    }
    private val textForClockOutAction: String by lazy {
        getStringWithResourceId(R.string.notification_pause_action_clock_out)
    }

    @DrawableRes
    override val smallIcon: Int = R.drawable.ic_pause_notification

    override val shouldUseChronometer: Boolean
        get() = useChronometer

    override val whenForChronometer: Long
        get() {
            val currentTimestamp = Date().time
            return currentTimestamp - registeredTime
        }

    init {
        if (this.useChronometer) {
            populateRegisteredTime()
        }
    }

    private fun populateRegisteredTime() {
        useChronometer = true

        try {
            var accumulatedTime = 0L

            for (timeInterval in getRegisteredTime()) {
                accumulatedTime += timeInterval.time
            }

            registeredTime = includeActiveTime(accumulatedTime)
        } catch (e: DomainException) {
            Timber.w(e, "Unable to populate registered time")
            useChronometer = false
        }
    }

    private fun getRegisteredTime(): List<TimeInterval> {
        val registeredTimeUseCase = buildRegisteredTimeUseCase(repository)

        return registeredTimeUseCase.invoke(
                project,
                TimeIntervalStartingPoint.DAY
        )
    }

    private fun includeActiveTime(registeredTime: Long): Long {
        val activeTimeInterval = activeTimeIntervalForProject
        return if (activeTimeInterval != null) {
            registeredTime + activeTimeInterval.interval
        } else registeredTime
    }

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

        fun build(context: Context, project: Project, useChronometer: Boolean): Notification {
            return PauseNotification(context, project, useChronometer)
                    .run { build() }
        }

        private fun buildRegisteredTimeUseCase(repository: TimeIntervalRepository): GetProjectTimeSince {
            return GetProjectTimeSince(repository)
        }
    }
}
