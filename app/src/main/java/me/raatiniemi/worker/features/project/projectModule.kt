/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project

import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.util.DigitalHoursMinutesIntervalFormat
import me.raatiniemi.worker.domain.util.FractionIntervalFormat
import me.raatiniemi.worker.features.project.model.ProjectHolder
import me.raatiniemi.worker.features.project.timereport.viewmodel.TimeReportViewModel
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_FRACTION
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val projectModule = module {
    single {
        ProjectHolder()
    }

    factory {
        val keyValueStore: KeyValueStore = get()
        val timeReportSummaryFormat = keyValueStore.int(
                AppKeys.TIME_REPORT_SUMMARY_FORMAT.rawValue,
                TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK
        )

        if (timeReportSummaryFormat == TIME_REPORT_SUMMARY_FORMAT_FRACTION) {
            FractionIntervalFormat()
        } else {
            DigitalHoursMinutesIntervalFormat()
        }
    }

    viewModel {
        TimeReportViewModel(
                get(),
                get(),
                get(),
                MarkRegisteredTime(get()),
                RemoveTime(get())
        )
    }
}
