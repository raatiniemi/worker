/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.feature.projects.createproject.view

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.WorkerTheme
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.usecase.CreateProject
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.feature.projects.createproject.view.CreateProjectScreen
import me.raatiniemi.worker.feature.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.koin.androidTestKoinModules
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

class CreateProjectScreenTest : AutoCloseKoinTest() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val createProject by inject<CreateProject>()

    private val vm by inject<CreateProjectViewModel>()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(androidTestKoinModules)
        }
    }

    @Test
    fun createProjectScreen_withEmptyProjectName() {
        composeTestRule.setContent {
            WorkerTheme {
                CreateProjectScreen(vm)
            }
        }

        composeTestRule.onNodeWithTag("Name")
            .performTextInput("")

        composeTestRule
            .onNodeWithTag("Error")
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag("Create")
            .assertIsNotEnabled()
    }

    @Test
    fun createProjectScreen_whenProjectAlreadyExists() {
        runBlocking {
            createProject(android.name)
        }
        composeTestRule.setContent {
            WorkerTheme {
                CreateProjectScreen(vm)
            }
        }

        composeTestRule.onNodeWithTag("Name")
            .performTextInput(android.name.value)

        composeTestRule
            .onNodeWithTag("Error")
            .assertExists()
        composeTestRule
            .onNodeWithTag("Create")
            .assertIsNotEnabled()
    }

    @Test
    fun createProjectScreen_whenCreateProject() {
        val actualViewActions = mutableListOf<CreateProjectViewActions>()
        composeTestRule.runOnUiThread {
            vm.viewActions.observeForever {
                actualViewActions.add(it)
            }
        }
        composeTestRule.setContent {
            WorkerTheme {
                CreateProjectScreen(vm)
            }
        }
        val expectedViewActions = listOf(
            CreateProjectViewActions.Created(android)
        )

        composeTestRule.onNodeWithTag("Name")
            .performTextInput(android.name.value)
        composeTestRule.onNodeWithTag("Create")
            .performClick()

        composeTestRule.onNodeWithTag("Error")
            .assertDoesNotExist()
        assertEquals(expectedViewActions, actualViewActions)
    }

    @Test
    fun createProjectScreen_whenDismiss() {
        val actualViewActions = mutableListOf<CreateProjectViewActions>()
        composeTestRule.runOnUiThread {
            vm.viewActions.observeForever {
                actualViewActions.add(it)
            }
        }
        composeTestRule.setContent {
            WorkerTheme {
                CreateProjectScreen(vm)
            }
        }
        val expectedViewActions = listOf(
            CreateProjectViewActions.Dismiss
        )

        composeTestRule.onNodeWithTag("Dismiss")
            .performClick()

        composeTestRule.onNodeWithTag("Error")
            .assertDoesNotExist()
        assertEquals(expectedViewActions, actualViewActions)
    }
}
