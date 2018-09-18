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

package me.raatiniemi.worker.features.settings

import me.raatiniemi.worker.features.settings.data.presenter.DataPresenter
import me.raatiniemi.worker.features.settings.project.presenter.ProjectPresenter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class Presenters : KoinComponent {
    val project: ProjectPresenter by inject()

    val data: DataPresenter by inject()
}
