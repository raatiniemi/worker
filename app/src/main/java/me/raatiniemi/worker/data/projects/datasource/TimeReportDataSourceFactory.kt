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

import androidx.paging.DataSource
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.usecase.CountTimeReports
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReports
import me.raatiniemi.worker.features.projects.model.ProjectProvider

internal class TimeReportDataSourceFactory(
    private val projectProvider: ProjectProvider,
    private val countTimeReports: CountTimeReports,
    private val findTimeReports: FindTimeReports
) : DataSource.Factory<Int, TimeReportDay>() {
    override fun create() = TimeReportDataSource(projectProvider, countTimeReports, findTimeReports)
}
