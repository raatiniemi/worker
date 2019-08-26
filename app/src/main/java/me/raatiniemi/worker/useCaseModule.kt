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

package me.raatiniemi.worker

import me.raatiniemi.worker.domain.project.usecase.FindActiveProjects
import me.raatiniemi.worker.domain.project.usecase.GetProject
import me.raatiniemi.worker.domain.project.usecase.IsProjectActive
import me.raatiniemi.worker.domain.timeinterval.usecase.CalculateTimeToday
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import org.koin.dsl.module.module

val useCaseModule = module {
    single {
        ClockIn(get())
    }

    single {
        ClockOut(get())
    }

    single {
        FindActiveProjects(get(), get())
    }

    single {
        GetProject(get())
    }

    single {
        IsProjectActive(get())
    }

    single {
        CalculateTimeToday(get())
    }
}
