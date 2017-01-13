/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.settings.view;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import me.raatiniemi.worker.R;
import timber.log.Timber;

import static me.raatiniemi.util.NullUtil.isNull;

abstract class BasePreferenceFragment extends PreferenceFragment {
    @Override
    public void onResume() {
        super.onResume();

        // Set the title for the preference fragment.
        getActivity().setTitle(getTitle());
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        if (preference instanceof PreferenceScreen) {
            switchPreferenceScreen(preference.getKey());
        } else {
            Timber.d("Preference '%s' is not implemented", preference.getTitle());
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.error_message_preference_not_implemented,
                    Snackbar.LENGTH_SHORT
            ).show();
        }
        return false;
    }

    /**
     * Switch the currently displayed preference screen.
     *
     * @param key Key for the new preference screen.
     */
    void switchPreferenceScreen(String key) {
        Timber.w("Switch to preference screen '%s' is not implemented", key);

        displayPreferenceScreenNotImplementedMessage();
    }

    private void displayPreferenceScreenNotImplementedMessage() {
        View contentView = getActivity().findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.error_message_preference_screen_not_implemented,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    /**
     * Get the resource id for the preference fragment title.
     *
     * @return Resource id for the preference fragment title.
     */
    @StringRes
    protected abstract int getTitle();
}
