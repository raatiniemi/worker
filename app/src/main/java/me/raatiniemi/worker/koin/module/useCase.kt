/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.koin.module

import me.raatiniemi.worker.domain.project.usecase.*
import me.raatiniemi.worker.domain.timeinterval.usecase.*
import me.raatiniemi.worker.domain.timereport.usecase.CountTimeReportWeeks
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReportWeeks
import org.koin.dsl.module

internal val useCase = module {
    single {
        CountProjects(get())
    }

    single {
        FindProjects(get())
    }

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
        CreateProject(get(), get())
    }

    single {
        GetProjectTimeSince(get())
    }

    single {
        FindProject(get())
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

    single {
        RemoveProject(get())
    }

    single {
        MarkRegisteredTime(get())
    }

    single {
        RemoveTime(get())
    }

    single {
        CountTimeReportWeeks(
            keyValueStore = get(),
            repository = get()
        )
    }

    single {
        FindTimeReportWeeks(
            keyValueStore = get(),
            repository = get()
        )
    }
}
