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

package me.raatiniemi.worker.features.projects.viewmodel

import androidx.lifecycle.ViewModel
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.util.RxUtil.hideErrors
import rx.Observable
import rx.subjects.PublishSubject

class RefreshActiveProjectsViewModel: ViewModel() {
    private val projects = PublishSubject.create<List<ProjectsItem>>()
    private val positions = PublishSubject.create<List<Int>>()

    init {
        projects.map { getPositionsForActiveProjects(it) }
                .compose(hideErrors())
                .subscribe(positions)
    }

    private fun getPositionsForActiveProjects(items: List<ProjectsItem>) =
            items.filter { it.isActive }
                    .map { items.indexOf(it) }

    fun projects(projects: List<ProjectsItem>) {
        this.projects.onNext(projects)
    }

    fun positionsForActiveProjects(): Observable<List<Int>> {
        return positions.asObservable()
    }
}
