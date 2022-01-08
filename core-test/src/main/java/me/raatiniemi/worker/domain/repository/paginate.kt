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

package me.raatiniemi.worker.domain.repository

import me.raatiniemi.worker.domain.model.LoadRange

internal fun <T> paginate(
    loadRange: LoadRange,
    elements: List<T>
): List<T> {
    val (position, size) = loadRange
    val fromIndex = indexWithCountCap(position.value, elements.count())
    val toIndex = indexWithCountCap(
        position.value + size.value,
        elements.count()
    )

    return elements.subList(fromIndex, toIndex)
}

/**
 * Use index unless it's above count, in which count will be used.
 */
private fun indexWithCountCap(index: Int, count: Int): Int {
    return if (index > count) {
        count
    } else {
        index
    }
}
