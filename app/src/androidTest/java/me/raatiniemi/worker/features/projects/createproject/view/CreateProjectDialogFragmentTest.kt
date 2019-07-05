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

package me.raatiniemi.worker.features.projects.createproject.view

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.R
import me.raatiniemi.worker.data.dataTestModule
import me.raatiniemi.worker.domain.model.android
import me.raatiniemi.worker.features.shared.view.isDisabled
import me.raatiniemi.worker.features.shared.view.withView
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.test.KoinTest
import java.util.concurrent.atomic.AtomicBoolean

// This needs to be a bit more than the actual delay used in
// the code, otherwise the test will be flakey.
private const val debounceDelayInMilliseconds = 300L
private const val createProjectDelayInMilliseconds = 100L

@RunWith(AndroidJUnit4::class)
class CreateProjectDialogFragmentTest : KoinTest {
    @Before
    fun setUp() {
        loadKoinModules(dataTestModule)
    }

    @Test
    fun createProjectIsNotEnabledByDefault() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            CreateProjectDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.btnCreate) {
            it.check(matches(isDisabled()))
        }
    }

    @Test
    fun createProjectIsEnabledWithProjectName() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            CreateProjectDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.etProjectName) {
            it.perform(replaceText(android.name.value))
        }

        withView(R.id.btnCreate) {
            Thread.sleep(debounceDelayInMilliseconds)
            it.check(matches(isEnabled()))
        }
    }

    @Test
    fun createProject() {
        val isProjectCreated = AtomicBoolean()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            CreateProjectDialogFragment.newInstance {
                isProjectCreated.compareAndSet(false, true)
            }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.etProjectName) {
            it.perform(replaceText(android.name.value))
        }

        withView(R.id.btnCreate) {
            Thread.sleep(debounceDelayInMilliseconds)
            it.perform(click())

            Thread.sleep(createProjectDelayInMilliseconds)
            assertTrue(isProjectCreated.get())
        }
    }

    @Test
    fun dismiss() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            CreateProjectDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.btnDismiss) {
            it.perform(click())
        }

        withView(R.id.btnDismiss) {
            it.check(doesNotExist())
        }
    }
}
