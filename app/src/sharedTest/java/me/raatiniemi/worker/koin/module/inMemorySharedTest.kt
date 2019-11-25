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

package me.raatiniemi.worker.koin.module

import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.dsl.module

internal val inMemorySharedTest = module(override = true) {
    // Key value store

    single {
        InMemoryKeyValueStore()
    }

    single<KeyValueStore> {
        get<InMemoryKeyValueStore>()
    }

    // Repository

    single<ProjectRepository> {
        ProjectInMemoryRepository()
    }

    single<TimeIntervalRepository> {
        TimeIntervalInMemoryRepository()
    }

    single<TimeReportRepository> {
        TimeReportInMemoryRepository(get())
    }

    // Analytics

    single {
        InMemoryUsageAnalytics()
    }

    single<UsageAnalytics> {
        get<InMemoryUsageAnalytics>()
    }
}
