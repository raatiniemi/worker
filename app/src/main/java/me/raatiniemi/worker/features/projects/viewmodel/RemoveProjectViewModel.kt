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

import me.raatiniemi.worker.domain.interactor.RemoveProject
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.util.RxUtil.hideErrors
import rx.Observable
import rx.subjects.PublishSubject

class RemoveProjectViewModel(private val removeProject: RemoveProject) {
    val removeProjectSuccess: PublishSubject<ProjectsItemAdapterResult> = PublishSubject.create<ProjectsItemAdapterResult>()
    val removeProjectError: PublishSubject<ProjectsItemAdapterResult> = PublishSubject.create<ProjectsItemAdapterResult>()

    private val project = PublishSubject.create<ProjectsItemAdapterResult>()

    init {
        project
                .switchMap { result ->
                    executeUseCase(result)
                            .compose(redirectErrorToSubject(result))
                            .compose(hideErrors())
                }
                .subscribe(removeProjectSuccess)
    }

    private fun redirectErrorToSubject(result: ProjectsItemAdapterResult): Observable.Transformer<ProjectsItemAdapterResult, ProjectsItemAdapterResult> {
        return Observable.Transformer { source ->
            source.doOnError { removeProjectError.onNext(result) }
                    .onErrorResumeNext(Observable.empty())
        }
    }

    private fun executeUseCase(result: ProjectsItemAdapterResult): Observable<ProjectsItemAdapterResult> {
        return try {
            val projectsItem = result.projectsItem
            removeProject.execute(projectsItem.asProject())

            Observable.just(result)
        } catch (e: Exception) {
            Observable.error(e)
        }
    }

    fun remove(result: ProjectsItemAdapterResult) {
        project.onNext(result)
    }
}
