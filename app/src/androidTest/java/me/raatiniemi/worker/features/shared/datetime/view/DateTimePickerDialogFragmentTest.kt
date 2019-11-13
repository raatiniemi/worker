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

package me.raatiniemi.worker.features.shared.datetime.view

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.shared.view.withView
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateTimePickerDialogFragmentTest {
    @Test
    fun date() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.tvDate) {
            it.check(matches(not(withText(""))))
        }
    }

    @Test
    fun time() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.tvTime) {
            it.check(matches(not(withText(""))))
        }
    }

    @Test
    fun chooseTimeIsVisibleByDefault() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.dpDate) {
            it.check(matches(not(isDisplayed())))
        }
        withView(R.id.tpTime) {
            it.check(matches(isDisplayed()))
        }
    }

    @Test
    fun chooseTime() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.tvTime) {
            it.perform(click())
        }

        withView(R.id.dpDate) {
            it.check(matches(not(isDisplayed())))
        }
        withView(R.id.tpTime) {
            it.check(matches(isDisplayed()))
        }
    }

    @Test
    fun chooseDate() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.tvDate) {
            it.perform(click())
        }

        withView(R.id.dpDate) {
            it.check(matches(isDisplayed()))
        }
        withView(R.id.tpTime) {
            it.check(matches(not(isDisplayed())))
        }
    }

    @Test(expected = RuntimeException::class)
    fun dismiss() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.btnCancel) {
            it.perform(click())
        }

        // When the fragment used for the scenario have been dismissed it throws an
        // `RuntimeException` with the root exception as it's cause.
        scenario.onFragment { }
    }
}
