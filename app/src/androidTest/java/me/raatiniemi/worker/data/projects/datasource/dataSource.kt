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

package me.raatiniemi.worker.data.projects.datasource

import androidx.paging.PositionalDataSource

internal fun loadInitialParams(
    requestedStartPosition: Int = 0,
    requestedLoadSize: Int = 10,
    pageSize: Int = requestedLoadSize,
    placeholdersEnabled: Boolean = true
) = PositionalDataSource.LoadInitialParams(
    requestedStartPosition,
    requestedLoadSize,
    pageSize,
    placeholdersEnabled
)

internal fun <T> loadInitialCallback(
    onResult: (PositionalDataSourceResult<T>) -> Unit
): PositionalDataSource.LoadInitialCallback<T> {
    return object : PositionalDataSource.LoadInitialCallback<T>() {
        override fun onResult(data: MutableList<T>, position: Int, totalCount: Int) {
            onResult(PositionalDataSourceResult(data, position, totalCount))
        }

        override fun onResult(data: MutableList<T>, position: Int) {
            onResult(PositionalDataSourceResult(data, position))
        }
    }
}
