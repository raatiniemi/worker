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

package me.raatiniemi.worker.monitor.analytics

import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import me.raatiniemi.worker.util.truncate

/**
 * The screen name can be at most 36 characters, due to a limitation with the
 * [FirebaseAnalytics.setCurrentScreen] method.
 */
private const val ALLOWED_MAX_LENGTH_FOR_SCREEN_NAME = 36

internal class ScreenName(internal val name: String) {
    override fun toString(): String {
        return name
    }

    companion object {
        val Empty = ScreenName("")
    }
}

internal fun name(screenName: ScreenName): String {
    return screenName.name
}

internal fun equal(lhs: ScreenName, rhs: ScreenName): Boolean {
    return lhs.name == rhs.name
}

/**
 * Builds a [ScreenName] from a [Fragment].
 */
internal fun screenName(fragment: Fragment): ScreenName {
    return ScreenName(truncate(fragment.javaClass.simpleName, ALLOWED_MAX_LENGTH_FOR_SCREEN_NAME))
}
