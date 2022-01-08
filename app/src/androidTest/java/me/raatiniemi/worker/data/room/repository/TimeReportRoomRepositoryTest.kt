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

package me.raatiniemi.worker.data.room.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.data.room.Database
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepositoryTest
import me.raatiniemi.worker.koin.androidTestKoinModules
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject

@Ignore("Tests are failing because missing proper timezone support")
@RunWith(AndroidJUnit4::class)
class TimeReportRoomRepositoryTest : TimeReportRepositoryTest(), KoinTest {
    private val database by inject<Database>()

    override val timeIntervals by inject<TimeIntervalRepository>()

    override val timeReports by inject<TimeReportRepository>()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules)
        }

        runBlocking {
            val projects = get<ProjectRepository>()
            projects.add(NewProject(android.name))
        }
    }

    @After
    fun tearDown() {
        database.close()
        stopKoin()
    }
}
