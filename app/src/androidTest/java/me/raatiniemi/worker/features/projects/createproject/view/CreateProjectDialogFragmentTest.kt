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

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.shared.view.isDisabled
import me.raatiniemi.worker.features.shared.view.withView
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateProjectDialogFragmentTest {
    @Test
    fun createProjectIsNotEnabledByDefault() {
        val scenario: FragmentScenario<CreateProjectDialogFragment> by lazy {
            launchFragmentInContainer(themeResId = R.style.Theme) {
                CreateProjectDialogFragment.newInstance { }
            }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.btnCreate) {
            it.check(matches(isDisabled()))
        }
    }
}
