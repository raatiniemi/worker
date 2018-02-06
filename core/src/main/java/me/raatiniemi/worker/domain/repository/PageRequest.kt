/*
 * Copyright (C) 2017 Worker Project
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

data class PageRequest internal constructor(val offset: Int, val maxResults: Int) {
    companion object {
        private const val MAX_RESULTS = 10

        fun withOffsetAndMaxResults(offset: Int, maxResults: Int): PageRequest {
            return PageRequest(offset, maxResults)
        }

        fun withOffset(offset: Int): PageRequest {
            return PageRequest.withOffsetAndMaxResults(offset, MAX_RESULTS)
        }

        fun withMaxResults(maxResults: Int): PageRequest {
            return PageRequest.withOffsetAndMaxResults(0, maxResults)
        }
    }
}
