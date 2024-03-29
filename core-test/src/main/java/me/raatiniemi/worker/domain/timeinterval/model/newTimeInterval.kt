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

package me.raatiniemi.worker.domain.timeinterval.model

import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.time.Milliseconds

data class NewTimeIntervalBuilder(
    var start: Milliseconds? = null
) {
    internal fun build(project: Project) = NewTimeInterval(
        projectId = project.id,
        start = requireNotNull(start)
    )
}

fun newTimeInterval(
    project: Project,
    configure: NewTimeIntervalBuilder.() -> Unit
): NewTimeInterval {
    val builder = NewTimeIntervalBuilder()
    builder.configure()

    return builder.build(project)
}
