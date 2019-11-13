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

package me.raatiniemi.worker.features.shared.model

import androidx.lifecycle.MutableLiveData
import me.raatiniemi.worker.util.isMainThread

internal operator fun <T> MutableLiveData<T>.plusAssign(value: T) {
    if (isMainThread) {
        setValue(value)
        return
    }

    postValue(value)
}

/**
 * "Reconfigures" a [MutableLiveData] source with a potentially new value.
 *
 * @param source Source to reconfigure with value.
 * @param configure Configurator for new value.
 */
internal fun <T> reconfigure(source: MutableLiveData<T>, configure: (T) -> T) {
    consume(source) { value ->
        source += configure(value)
    }
}
