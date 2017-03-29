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

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PreferenceUtilTest {
    private boolean checked;
    private final PreferenceUtil.ReadCheckBoxPreference toggle = isChecked -> this.checked = isChecked;

    @Test
    public void populateCheckBoxPreference_withCheckedValue() {
        CheckBoxPreference preference = mock(CheckBoxPreference.class);

        PreferenceUtil.populateCheckBoxPreference(preference, true);

        verify(preference).setChecked(eq(true));
    }

    @Test
    public void populateCheckBoxPreference_withUncheckedValue() {
        CheckBoxPreference preference = mock(CheckBoxPreference.class);

        PreferenceUtil.populateCheckBoxPreference(preference, false);

        verify(preference).setChecked(eq(false));
    }

    @Test
    public void readCheckBoxPreference_withInvalidPreferenceType() {
        ListPreference preference = mock(ListPreference.class);

        PreferenceUtil.readCheckBoxPreference(preference, toggle);

        assertFalse(checked);
    }

    @Test
    public void readCheckBoxPreference_withCheckedPreference() {
        CheckBoxPreference preference = mock(CheckBoxPreference.class);
        when(preference.isChecked()).thenReturn(true);

        PreferenceUtil.readCheckBoxPreference(preference, toggle);

        assertTrue(checked);
    }

    @Test
    public void readCheckBoxPreference_withUncheckedPreference() {
        CheckBoxPreference preference = mock(CheckBoxPreference.class);
        when(preference.isChecked()).thenReturn(false);

        PreferenceUtil.readCheckBoxPreference(preference, toggle);

        assertFalse(checked);
    }
}
