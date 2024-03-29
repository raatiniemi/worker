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

import java.util.*

@JvmInline
value class Milliseconds(val value: Long) : Comparable<Milliseconds> {
    operator fun plus(other: Milliseconds) = Milliseconds(value + other.value)

    operator fun minus(other: Milliseconds) = Milliseconds(value - other.value)

    operator fun plus(value: Long) = Milliseconds(this.value + value)

    operator fun minus(value: Long) = Milliseconds(this.value - value)

    override fun compareTo(other: Milliseconds) = value.compareTo(other.value)

    companion object {
        val now: Milliseconds
            get() = Milliseconds(Date().time)

        val empty = Milliseconds(0)
    }
}
