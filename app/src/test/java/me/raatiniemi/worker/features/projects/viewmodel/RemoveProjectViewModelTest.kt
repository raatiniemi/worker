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
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import rx.observers.TestSubscriber

@RunWith(JUnit4::class)
class RemoveProjectViewModelTest {
    private val repository = ProjectInMemoryRepository()
    private val removeProjectSuccess: TestSubscriber<ProjectsItemAdapterResult> = TestSubscriber()
    private val removeProjectError: TestSubscriber<ProjectsItemAdapterResult> = TestSubscriber()

    private lateinit var removeProject: RemoveProject
    private lateinit var vm: RemoveProjectViewModel

    @Before
    fun setUp() {
        removeProject = RemoveProject(repository)
        vm = RemoveProjectViewModel(removeProject)

        vm.removeProjectSuccess.subscribe(removeProjectSuccess)
        vm.removeProjectError.subscribe(removeProjectError)
    }

    @Test
    fun `remove project without project`() {
        val project = Project.from("Name")
        val item = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, item)

        vm.remove(result)

        removeProjectSuccess.assertNoValues()
        removeProjectSuccess.assertNoTerminalEvent()
        removeProjectError.assertValue(result)
    }

    @Test
    fun `remove project with project`() {
        val project = Project(null, "Name")
        val item = ProjectsItem.from(project.copy(1), emptyList())
        val result = ProjectsItemAdapterResult(0, item)
        repository.add(project)

        vm.remove(result)

        removeProjectSuccess.assertValue(result)
        removeProjectSuccess.assertNoTerminalEvent()
        removeProjectError.assertNoValues()
        removeProjectError.assertNoTerminalEvent()
    }
}
