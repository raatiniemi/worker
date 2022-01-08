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

package me.raatiniemi.worker.feature.ongoing.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OngoingUriCommunicatorTest {
    @Test
    fun parseProjectIdFromUri_withoutUri() {
        val actual = OngoingUriCommunicator.parseFrom(null)

        assertEquals(0, actual)
    }

    @Test
    fun parseProjectIdFrom_withUri() {
        val uri = OngoingUriCommunicator.createWith(2)

        val actual = OngoingUriCommunicator.parseFrom(uri)

        assertEquals(2, actual)
    }
}
