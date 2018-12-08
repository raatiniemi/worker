/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class ConsumableLiveData<T> : MutableLiveData<T>() {
    private val consumed = AtomicBoolean(false)

    @MainThread
    fun observeAndConsume(owner: LifecycleOwner, observer: Observer<T>) {
        if (hasActiveObservers()) {
            Timber.d("Multiple observers are registered but only one will be notified of the change")
        }

        super.observe(owner, Observer {
            if (consumed.compareAndSet(false, true)) {
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    override fun setValue(value: T) {
        consumed.set(false)

        super.setValue(value)
    }
}
