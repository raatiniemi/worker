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

package me.raatiniemi.worker.domain.time

val Long.milliseconds: Long
    get() = this

val Long.seconds: Long
    get() = milliseconds * MILLISECONDS_IN_SECOND

val Long.minutes: Long
    get() = seconds * SECONDS_IN_MINUTE

val Long.hours: Long
    get() = minutes * MINUTES_IN_HOUR

val Long.days: Long
    get() = hours * HOURS_IN_DAY

val Long.weeks: Long
    get() = days * DAYS_IN_WEEK
