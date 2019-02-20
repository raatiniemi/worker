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

package me.raatiniemi.worker.util;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import timber.log.Timber;

public class PreferenceUtil {
    private PreferenceUtil() {
    }

    public static void readCheckBoxPreference(
            @NonNull Preference preference,
            @NonNull ReadCheckBoxPreference readCheckBoxPreference
    ) {
        try {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            boolean isChecked = checkBoxPreference.isChecked();

            readCheckBoxPreference.apply(isChecked);
        } catch (ClassCastException e) {
            Timber.w(e, "Unable to read value from '%s'", preference.getKey());
        }
    }

    @FunctionalInterface
    public interface ReadCheckBoxPreference {
        void apply(boolean isChecked);
    }
}
