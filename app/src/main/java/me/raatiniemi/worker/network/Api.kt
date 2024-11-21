/*
 * Copyright (C) 2024 Tobias Raatiniemi
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

package me.raatiniemi.worker.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
internal data class UploadRequest(
    val projects: List<Project>
) {
    companion object {
        @JsonClass(generateAdapter = true)
        data class Project(
            @Json(name = "id")
            val id: Long,
            @Json(name = "name")
            val name: String,
            @Json(name = "time_intervals")
            val timeIntervals: List<TimeInterval>,
        )

        @JsonClass(generateAdapter = true)
        data class TimeInterval(
            @Json(name = "id")
            val id: Long,
            @Json(name = "start_in_milliseconds")
            val start: Long,
            @Json(name = "stop_in_milliseconds")
            val stop: Long,
            @Json(name = "is_registered")
            val isRegistered: Boolean,
        )
    }
}

internal interface Api {
    @POST("/android/backup")
    suspend fun upload(@Body request: UploadRequest)
}
