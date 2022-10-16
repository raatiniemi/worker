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

package me.raatiniemi.worker.monitor.analytics

internal sealed class Event(
    val name: EventName,
    val parameters: List<EventParameter> = emptyList()
) {
    constructor(name: EventName, parameter: EventParameter) : this(name, listOf(parameter))

    object TapProjectOpen : Event(TapProjectEvent.Open)
    object TapProjectToggle : Event(TapProjectEvent.Toggle)
    object TapProjectAt : Event(TapProjectEvent.At)
    object TapProjectRemove : Event(TapProjectEvent.Remove)

    object ProjectCreate : Event(ProjectEvent.Create)

    object ProjectClockIn :
        Event(ProjectEvent.ClockIn, EventParameter.Source(ClockInOutSource.Card))

    object ProjectClockOut :
        Event(ProjectEvent.ClockOut, EventParameter.Source(ClockInOutSource.Card))

    object ProjectRemove : Event(ProjectEvent.Remove)

    data class TimeReportToggle(private val count: Int) :
        Event(TimeReportEvent.Toggle, EventParameter.Count(count))

    data class TimeReportRemove(private val count: Int) :
        Event(TimeReportEvent.Remove, EventParameter.Count(count))
}
