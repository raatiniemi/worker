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

package me.raatiniemi.worker.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static me.raatiniemi.worker.util.NullUtil.isNull;

/**
 * Enable display of hint for images via long click.
 */
public class HintedImageButtonListener implements View.OnLongClickListener {
    /**
     * Context to use.
     */
    private final Context context;

    /**
     * Constructor.
     *
     * @param context Context to use.
     */
    public HintedImageButtonListener(@NonNull final Context context) {
        this.context = context;
    }

    /**
     * Display the content description of the view in a toast message.
     *
     * @param view View pressed by the user.
     * @return True if the long click was consumed, otherwise false.
     */
    @Override
    public boolean onLongClick(@Nullable final View view) {
        if (isNull(view)) {
            return false;
        }

        // Check that the view actually have content description to display.
        final CharSequence description = view.getContentDescription();
        if (TextUtils.isEmpty(description)) {
            return false;
        }

        // Retrieve the position of the view.
        int[] position = new int[2];
        view.getLocationInWindow(position);

        final Toast toast = Toast.makeText(context, description, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.START, position[0], position[1]);
        toast.show();

        return true;
    }
}
