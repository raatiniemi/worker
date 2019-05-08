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

package me.raatiniemi.worker.features.projects.all.model

import android.content.res.Resources
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.util.DateIntervalFormat
import me.raatiniemi.worker.domain.util.HoursMinutesIntervalFormat
import java.text.SimpleDateFormat
import java.util.*

data class ProjectsItem(
    private val project: Project,
    private val registeredTime: List<TimeInterval>
) {
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"))
    private val registeredTimeSummary: Long
    private val activeTimeInterval: TimeInterval?

    val title: String
        get() = project.name

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
        get() = activeTimeInterval?.startInMilliseconds ?: 0

    init {
        registeredTimeSummary = calculateSummaryFromRegisteredTime(registeredTime)
        activeTimeInterval = findActiveTimeInterval(registeredTime)
    }

    fun asProject() = project

    private fun calculateTimeSummary(): Long {
        if (activeTimeInterval == null) {
            return registeredTimeSummary
        }

        return registeredTimeSummary + activeTimeInterval.interval
    }

    fun getClockedInSince(resources: Resources): String? {
        val activeTimeInterval = this.activeTimeInterval ?: return null

        return String.format(
            Locale.forLanguageTag("en_US"),
            getClockedInSinceFormatTemplate(resources),
            formattedClockedInSince,
            formattedElapsedTime(activeTimeInterval.interval)
        )
    }

    companion object {
        private val intervalFormat: DateIntervalFormat = HoursMinutesIntervalFormat()

        private fun calculateSummaryFromRegisteredTime(registeredTime: List<TimeInterval>): Long {
            return registeredTime.map { it.time }.sum()
        }

        private fun findActiveTimeInterval(registeredTime: List<TimeInterval>): TimeInterval? {
            return registeredTime.firstOrNull { it.isActive }
        }

        private fun formattedElapsedTime(elapsedTimeInMilliseconds: Long): String {
            return intervalFormat.format(elapsedTimeInMilliseconds)
        }

        private fun getClockedInSinceFormatTemplate(resources: Resources): String {
            return resources.getString(R.string.projects_all_clocked_in_since)
        }
    }
}