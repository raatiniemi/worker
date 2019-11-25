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

package me.raatiniemi.worker.feature.projects.all.model

import android.content.res.Resources
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.date.DateIntervalFormat
import me.raatiniemi.worker.domain.date.HoursMinutesIntervalFormat
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.calculateInterval
import me.raatiniemi.worker.domain.timeinterval.model.calculateTime
import me.raatiniemi.worker.domain.timeinterval.model.isActive
import java.text.SimpleDateFormat
import java.util.*

data class ProjectsItem(
    private val project: Project,
    private val registeredTime: List<TimeInterval>
) {
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"))
    private val registeredTimeSummary: Milliseconds
    private val activeTimeInterval: TimeInterval?

    val title: String
        get() = project.name.value

    val isActive: Boolean
        get() = activeTimeInterval != null

    val timeSummary: String
        get() = intervalFormat.format(calculateTimeSummary())

    private val formattedClockedInSince: String
        get() {
            val date = Date(clockedInSinceInMilliseconds)
            return timeFormat.format(date)
        }

    val clockedInSinceInMilliseconds: Long
        get() = activeTimeInterval?.start?.value ?: 0

    init {
        registeredTimeSummary = calculateSummaryFromRegisteredTime(registeredTime)
        activeTimeInterval = findActiveTimeInterval(registeredTime)
    }

    fun asProject() = project

    private fun calculateTimeSummary(): Milliseconds {
        if (activeTimeInterval == null) {
            return registeredTimeSummary
        }

        return registeredTimeSummary + calculateInterval(activeTimeInterval)
    }

    fun getClockedInSince(resources: Resources): String? {
        val activeTimeInterval = this.activeTimeInterval ?: return null

        return String.format(
            Locale.forLanguageTag("en_US"),
            getClockedInSinceFormatTemplate(resources),
            formattedClockedInSince,
            formattedElapsedTime(calculateInterval(activeTimeInterval))
        )
    }

    companion object {
        private val intervalFormat: DateIntervalFormat = HoursMinutesIntervalFormat()

        private fun calculateSummaryFromRegisteredTime(registeredTime: List<TimeInterval>): Milliseconds {
            return registeredTime.map { calculateTime(it) }
                .fold(Milliseconds.empty) { total, next -> total + next }
        }

        private fun findActiveTimeInterval(registeredTime: List<TimeInterval>): TimeInterval? {
            return registeredTime.firstOrNull { isActive(it) }
        }

        private fun formattedElapsedTime(elapsedTime: Milliseconds): String {
            return intervalFormat.format(elapsedTime)
        }

        private fun getClockedInSinceFormatTemplate(resources: Resources): String {
            return resources.getString(R.string.projects_all_clocked_in_since)
        }
    }
}
