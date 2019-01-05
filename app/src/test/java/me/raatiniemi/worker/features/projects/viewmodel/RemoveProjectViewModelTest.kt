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
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import rx.observers.TestSubscriber

@RunWith(JUnit4::class)
class RemoveProjectViewModelTest {
    private val removeProjectSuccess: TestSubscriber<ProjectsItemAdapterResult> = TestSubscriber()
    private val removeProjectError: TestSubscriber<ProjectsItemAdapterResult> = TestSubscriber()

    private lateinit var removeProject: RemoveProject
    private lateinit var vm: RemoveProjectViewModel

    @Before
    fun setUp() {
        removeProject = mock(RemoveProject::class.java)
        vm = RemoveProjectViewModel(removeProject)

        vm.removeProjectSuccess.subscribe(removeProjectSuccess)
        vm.removeProjectError.subscribe(removeProjectError)
    }

    @Test
    fun deleteProject_withError() {
        val project = Project.from("Name")
        val item = ProjectsItem.from(project, emptyList<TimeInterval>())
        val result = ProjectsItemAdapterResult(0, item)
        doThrow(RuntimeException::class.java)
                .`when`<RemoveProject>(removeProject).execute(any())

        vm.remove(result)

        removeProjectSuccess.assertNoValues()
        removeProjectSuccess.assertNoTerminalEvent()
        removeProjectError.assertValue(result)
    }

    @Test
    fun deleteProject() {
        val project = Project.from("Name")
        val item = ProjectsItem.from(project, emptyList<TimeInterval>())
        val result = ProjectsItemAdapterResult(0, item)

        vm.remove(result)

        removeProjectSuccess.assertValue(result)
        removeProjectSuccess.assertNoTerminalEvent()
        removeProjectError.assertNoValues()
        removeProjectError.assertNoTerminalEvent()
    }
}
