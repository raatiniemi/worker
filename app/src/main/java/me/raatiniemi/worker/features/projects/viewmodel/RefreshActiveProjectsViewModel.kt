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

import androidx.annotation.NonNull
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.util.RxUtil.hideErrors
import rx.Observable
import rx.subjects.PublishSubject

interface RefreshActiveProjectsViewModel {
    interface Input {
        fun projects(@NonNull projects: List<ProjectsItem>)
    }

    interface Output {
        fun positionsForActiveProjects(): Observable<List<Int>>
    }

    class ViewModel : Input, Output {
        private val input: Input
        private val output: Output

        private val projects = PublishSubject.create<List<ProjectsItem>>()
        private val positions = PublishSubject.create<List<Int>>()

        init {
            input = this
            output = this

            projects.map { getPositionsForActiveProjects(it) }
                    .compose(hideErrors())
                    .subscribe(positions)
        }

        private fun getPositionsForActiveProjects(items: List<ProjectsItem>) =
                items.filter { it.isActive }
                        .map { items.indexOf(it) }

        override fun projects(projects: List<ProjectsItem>) {
            this.projects.onNext(projects)
        }

        override fun positionsForActiveProjects(): Observable<List<Int>> {
            return positions.asObservable()
        }

        fun input(): Input {
            return input
        }

        fun output(): Output {
            return output
        }
    }
}
