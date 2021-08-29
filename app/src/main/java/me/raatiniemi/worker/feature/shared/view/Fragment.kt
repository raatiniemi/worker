/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.raatiniemi.worker.feature.shared.model.ConsumableLiveData
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Observe values emitted from a [LiveData] source.
 *
 * @param source [LiveData] source to observe emitted values.
 * @param consumer Closure for consuming emitted values.
 */
internal fun <T> Fragment.observe(source: LiveData<T>, consumer: (T) -> Unit) {
    source.observe(viewLifecycleOwner, {
        consumer(it)
    })
}

/**
 * Observe and consume values emitted from a [ConsumableLiveData] source with consuming closure.
 *
 * @param source [ConsumableLiveData] source to observe emitted values.
 * @param consumer Closure for consuming emitted values.
 */
internal fun <T> Fragment.observeAndConsume(source: ConsumableLiveData<T>, consumer: (T) -> Unit) {
    source.observeAndConsume(viewLifecycleOwner, {
        consumer(it)
    })
}

/**
 * Allow for easier launch coroutine with correct lifecycle scope from [Fragment].
 *
 * @param block Suspending call that should be launched.
 */
internal fun Fragment.launch(block: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope
        .launch { block() }
}

/**
 * Attempt to require activity from fragment and pass it to a consuming closure.
 *
 * The function is using explicit inlining in an attempt to not distort the call stack for if
 * the call to [Fragment.requireActivity] throws an exception.
 *
 * @param fragment Fragment from which to require the activity.
 * @param consumer Closure for consuming the activity.
 */
internal inline fun requireActivity(
    fragment: Fragment,
    crossinline consumer: (FragmentActivity) -> Unit
) {
    try {
        consumer(fragment.requireActivity())
    } catch (e: IllegalStateException) {
        Timber.w(e, "Unable to require activity from fragment: ${fragment.javaClass.simpleName}")
    }
}

/**
 * Set an action bar title for a [Fragment].
 *
 * @receiver [Fragment] Fragment on which to set the title.
 * @param title Title to set in the [Fragment] action bar.
 */
internal fun Fragment.setTitle(title: String) {
    requireActivity(this) { activity ->
        try {
            check(activity is AppCompatActivity) {
                "Unexpected type of activity: ${activity.javaClass.simpleName}"
            }
            val supportActionBar = requireNotNull(activity.supportActionBar) {
                "No action bar is configured"
            }

            supportActionBar.title = title
        } catch (e: IllegalStateException) {
            Timber.w(e, "Unable to set title on fragment")
        } catch (e: IllegalArgumentException) {
            Timber.w(e, "Unable to set title on fragment")
        }
    }
}

internal fun <T : Fragment> tag(reference: KClass<T>): String {
    return reference.java.simpleName
}
