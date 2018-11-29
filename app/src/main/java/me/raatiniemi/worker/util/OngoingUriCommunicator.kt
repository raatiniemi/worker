/*
 * Copyright (C) 2018 Worker Project
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

package me.raatiniemi.worker.util

import android.net.Uri

class OngoingUriCommunicator {
    companion object {
        @JvmStatic
        fun createWith(projectId: Long): Uri {
            return Uri.parse("projects/$projectId")
        }

        @JvmStatic
        fun parseFrom(uri: Uri?): Long {
            if (null == uri) {
                return 0
            }

            val projectId = uri.pathSegments[1]
            return projectId.toLong()
        }
    }
}
