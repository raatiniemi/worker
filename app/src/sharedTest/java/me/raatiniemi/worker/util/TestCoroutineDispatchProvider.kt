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

package me.raatiniemi.worker.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
internal class TestCoroutineDispatchProvider(
    private val dispatcher: TestCoroutineDispatcher
) : CoroutineDispatchProvider {
    override fun main(): CoroutineDispatcher {
        return dispatcher
    }

    override fun default(): CoroutineDispatcher {
        return dispatcher
    }

    override fun io(): CoroutineDispatcher {
        return dispatcher
    }

    override fun unconfined(): CoroutineDispatcher {
        return dispatcher
    }
}
