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

package me.raatiniemi.worker.data

import me.raatiniemi.worker.data.repository.ProjectResolverRepository
import me.raatiniemi.worker.data.repository.TimeResolverRepository
import me.raatiniemi.worker.data.repository.TimesheetResolverRepository
import me.raatiniemi.worker.domain.repository.ProjectRepository
import me.raatiniemi.worker.domain.repository.TimeRepository
import me.raatiniemi.worker.domain.repository.TimesheetRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val dataModule = module {
    single<ProjectRepository> { ProjectResolverRepository(androidContext().contentResolver) }

    single<TimeRepository> { TimeResolverRepository(androidContext().contentResolver) }

    single<TimesheetRepository> { TimesheetResolverRepository(androidContext().contentResolver) }
}