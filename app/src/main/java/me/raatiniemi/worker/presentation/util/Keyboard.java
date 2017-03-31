/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.presentation.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodManager;

import timber.log.Timber;

import static me.raatiniemi.util.NullUtil.isNull;

public class Keyboard {
    /**
     * Store the InputMethodManager.
     */
    private static InputMethodManager inputMethodManager;

    private Keyboard() {
    }

    /**
     * Retrieve the InputMethodManager.
     *
     * @param context Context used to retrieve the InputMethodManager.
     * @return InputMethodManager if we're able to retrieve it, otherwise null.
     */
    @Nullable
    private static InputMethodManager getInputMethodManager(@NonNull Context context) {
        // If we don't have the input method manager available,
        // we have to retrieve it from the context.
        if (isNull(inputMethodManager)) {
            try {
                inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            } catch (ClassCastException e) {
                Timber.w(e, "Unable to cast the Context.INPUT_METHOD_SERVICE to InputMethodManager");
            }
        }

        return inputMethodManager;
    }

    /**
     * Forcing the keyboard to show.
     *
     * @param context Context used when showing the keyboard.
     */
    public static void show(@NonNull Context context) {
        // Check that we have the input method manager available.
        InputMethodManager manager = getInputMethodManager(context);
        if (isNull(manager)) {
            Timber.w("Unable to retrieve the InputMethodManager");
            return;
        }

        manager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
