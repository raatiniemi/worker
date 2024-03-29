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

package me.raatiniemi.worker.feature.shared.datetime.view

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.feature.shared.view.withView
import me.raatiniemi.worker.koin.androidTestKoinModules
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import java.util.*

@RunWith(AndroidJUnit4::class)
class DateTimePickerDialogFragmentTest : AutoCloseKoinTest() {
    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules)
        }
    }

    // Date

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

    // Time

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

    // Choose time

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

    // Choose date

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

    // Choose

    @Test
    fun choose_withInitialValues() {
        val now = Date()
        var actual: Date? = null
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance {
                it.date = now
                it.choose = { date ->
                    actual = date
                }
            }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.btnOk) {
            it.perform(click())
        }

        assertEquals(now, actual)
    }

    @Test
    fun choose_withChosenTime() {
        val now = Date()
        var actual: Date? = null
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance {
                it.date = now
                it.choose = { date ->
                    actual = date
                }
            }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)
        withView(R.id.tpTime) {
            it.check(matches(isDisplayed()))
            it.perform(PickerActions.setTime(10, 33))
        }
        val hoursMinutes = HoursMinutes(10, 33)
        val expected = date(now, hoursMinutes)

        withView(R.id.btnOk) {
            it.perform(click())
        }

        assertEquals(expected, actual)
    }

    @Test
    fun choose_withChosenDate() {
        val now = Date()
        var actual: Date? = null
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance {
                it.date = now
                it.choose = { date ->
                    actual = date
                }
            }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)
        withView(R.id.tpTime) {
            it.check(matches(isDisplayed()))
        }
        withView(R.id.tvDate) {
            it.perform(click())
        }
        withView(R.id.dpDate) {
            it.check(matches(isDisplayed()))
            it.perform(PickerActions.setDate(2015, 1, 18))
        }
        val yearsMonthsDays = yearsMonthsDays(Years(2015), Months(0), Days(18))
        val expected = date(now, yearsMonthsDays)

        withView(R.id.btnOk) {
            it.perform(click())
        }

        assertEquals(expected, actual)
    }

    @Test
    fun choose_withChosenTimeAndDate() {
        val now = Date()
        var actual: Date? = null
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance {
                it.date = now
                it.choose = { date ->
                    actual = date
                }
            }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)
        withView(R.id.tpTime) {
            it.check(matches(isDisplayed()))
            it.perform(PickerActions.setTime(10, 33))
        }
        withView(R.id.tvDate) {
            it.perform(click())
        }
        withView(R.id.dpDate) {
            it.check(matches(isDisplayed()))
            it.perform(PickerActions.setDate(2015, 1, 18))
        }
        val yearsMonthsDays = yearsMonthsDays(Years(2015), Months(0), Days(18))
        val hoursMinutes = HoursMinutes(10, 33)
        val expected = date(date(now, yearsMonthsDays), hoursMinutes)

        withView(R.id.btnOk) {
            it.perform(click())
        }

        assertEquals(expected, actual)
    }

    @Test(expected = RuntimeException::class)
    fun choose_withDismiss() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme) {
            DateTimePickerDialogFragment.newInstance { }
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        withView(R.id.btnOk) {
            it.perform(click())
        }

        // When the fragment used for the scenario have been dismissed it throws an
        // `RuntimeException` with the root exception as it's cause.
        scenario.onFragment { }
    }

    // Dismiss

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
