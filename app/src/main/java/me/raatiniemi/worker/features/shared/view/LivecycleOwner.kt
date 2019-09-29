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

package me.raatiniemi.worker.features.shared.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData

internal fun <T> LifecycleOwner.observe(data: LiveData<T>, block: (T) -> Unit) {
    data.observe(this, Observer {
        block(it)
    })
}

internal fun <T> LifecycleOwner.observeAndConsume(data: ConsumableLiveData<T>, block: (T) -> Unit) {
    data.observeAndConsume(this, Observer {
        block(it)
    })
}