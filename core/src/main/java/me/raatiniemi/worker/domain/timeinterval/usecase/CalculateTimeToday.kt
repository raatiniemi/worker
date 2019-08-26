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

package me.raatiniemi.worker.domain.timeinterval.usecase

import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.model.calculateInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository

class CalculateTimeToday(private val repository: TimeIntervalRepository) {
    operator fun invoke(project: Project, stopForActive: Milliseconds = Milliseconds.now): Long {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        val timeIntervals = repository.findAll(project, startingPoint)
        return timeIntervals.map { calculateInterval(it, stopForActive) }
            .map { it.value }
            .sum()
    }
}
