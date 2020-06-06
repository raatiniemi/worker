/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.shared.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import me.raatiniemi.worker.domain.time.milliseconds
import me.raatiniemi.worker.feature.shared.model.observeNonNull
import me.raatiniemi.worker.feature.shared.model.plusAssign
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RefreshTimeIntervalLifecycleObserverTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    @Ignore("Running test in CI/CD fails due to hardware restrictions")
    @Test
    fun `on resume and pause`() {
        val wasCalled = MutableLiveData(false)
        val observer = RefreshTimeIntervalLifecycleObserver(1.milliseconds) {
            wasCalled += true
        }
        val expected = true

        observer.onResume()

        wasCalled.observeNonNull { actual ->
            observer.onPause()
            assertEquals(expected, actual)
        }
    }
}
