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

package me.raatiniemi.worker.data.datasource

import androidx.paging.PagingSource
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize

internal fun calculateLoadRange(params: PagingSource.LoadParams<Int>): LoadRange {
    val pageKey = params.key ?: 0
    return LoadRange(
        LoadPosition(pageKey),
        LoadSize(params.loadSize)
    )
}

internal fun calculateNextKey(loadRange: LoadRange, total: Int): Int? {
    val nextPosition = loadRange.position.value + loadRange.size.value
    return if (nextPosition < total) {
        nextPosition
    } else {
        null
    }
}
