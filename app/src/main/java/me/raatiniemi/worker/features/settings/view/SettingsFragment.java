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

package me.raatiniemi.worker.features.settings.view;

import android.app.Fragment;
import android.os.Bundle;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.features.settings.data.view.DataFragment;
import me.raatiniemi.worker.features.settings.project.view.ProjectFragment;

public class SettingsFragment extends BasePreferenceFragment {
    /**
     * Key for the project preference.
     */
    private static final String SETTINGS_PROJECT_KEY = "settings_project";

    /**
     * Key for the data preference.
     */
    private static final String SETTINGS_DATA_KEY = "settings_data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    protected void switchPreferenceScreen(String key) {
        Fragment fragment;
        switch (key) {
            case SETTINGS_PROJECT_KEY:
                fragment = new ProjectFragment();
                break;
            case SETTINGS_DATA_KEY:
                fragment = new DataFragment();
                break;
            default:
                super.switchPreferenceScreen(key);
                return;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, key)
                .addToBackStack(key)
                .commit();
    }

    @Override
    public int getTitle() {
        return R.string.activity_settings_title;
    }
}
