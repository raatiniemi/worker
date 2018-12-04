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

package me.raatiniemi.worker.features.projects.createproject.viewmodel

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException
import me.raatiniemi.worker.domain.interactor.CreateProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.validator.ProjectName
import me.raatiniemi.worker.util.RxUtil.hideErrors
import me.raatiniemi.worker.util.RxUtil.redirectErrors
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject

interface CreateProjectViewModel {
    interface Input {
        fun projectName(name: String)

        fun createProject()
    }

    interface Output {
        val isProjectNameValid: Observable<Boolean>

        val createProjectSuccess: Observable<Project>
    }

    interface Error {
        val invalidProjectNameError: Observable<String>

        val duplicateProjectNameError: Observable<String>

        val createProjectError: Observable<String>
    }

    class ViewModel(private val useCase: CreateProject) : Input, Output, Error {
        val input: Input = this
        val output: Output = this
        val error: Error = this

        private val _projectName = PublishSubject.create<String>()

        private val _isProjectNameValid = BehaviorSubject.create(false)
        override val isProjectNameValid: Observable<Boolean> = _isProjectNameValid

        private val _createProject = PublishSubject.create<Void>()

        private val _createProjectSuccess = PublishSubject.create<Project>()
        override val createProjectSuccess: Observable<Project> = _createProjectSuccess.asObservable()

        private val _createProjectError = PublishSubject.create<Throwable>()

        override val invalidProjectNameError: Observable<String> = _createProjectError
                .filter { isInvalidProjectNameError(it) }
                .map { it.message }

        private fun isInvalidProjectNameError(e: Throwable): Boolean {
            return e is InvalidProjectNameException
        }

        override val duplicateProjectNameError: Observable<String> = _createProjectError
                .filter { isDuplicateProjectNameError(it) }
                .map { it.message }

        private fun isDuplicateProjectNameError(e: Throwable): Boolean {
            return e is ProjectAlreadyExistsException
        }

        override val createProjectError: Observable<String> = _createProjectError
                .filter { isUnknownError(it) }
                .map { it.message }

        private fun isUnknownError(e: Throwable): Boolean {
            return !isInvalidProjectNameError(e) && !isDuplicateProjectNameError(e)
        }

        init {
            _projectName.map(ProjectName::isValid)
                    .subscribe(_isProjectNameValid)

            _createProject.withLatestFrom(_projectName) { _, name -> name }
                    .switchMap { name ->
                        executeUseCase(name)
                                .compose(redirectErrors(_createProjectError))
                                .compose(hideErrors())
                    }
                    .subscribe(_createProjectSuccess)
        }

        private fun executeUseCase(name: String): Observable<Project> {
            return try {
                val project = Project.from(name)

                Observable.just(useCase.execute(project))
            } catch (e: Exception) {
                Observable.error(e)
            }
        }

        override fun projectName(name: String) = _projectName.onNext(name)

        override fun createProject() = _createProject.onNext(null)
    }
}
