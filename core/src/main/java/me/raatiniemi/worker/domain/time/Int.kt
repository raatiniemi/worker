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

package me.raatiniemi.worker.domain.time

val Int.milliseconds: Long
    get() = toLong().milliseconds

val Int.seconds: Long
    get() = toLong().seconds

val Int.minutes: Long
    get() = toLong().minutes

val Int.hours: Long
    get() = toLong().hours

val Int.days: Long
    get() = toLong().days

val Int.weeks: Long
    get() = toLong().weeks
