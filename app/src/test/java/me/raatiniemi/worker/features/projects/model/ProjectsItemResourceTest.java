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

package me.raatiniemi.worker.features.projects.model;

import android.content.res.Resources;

import me.raatiniemi.worker.R;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class ProjectsItemResourceTest {
    Resources getResources() {
        Resources resources = mock(Resources.class);
        when(resources.getString(eq(R.string.fragment_projects_item_clock_in), anyString())).thenReturn("Clock in %s now");
        when(resources.getString(eq(R.string.fragment_projects_item_clock_out), anyString())).thenReturn("Clock out %s now");

        when(resources.getString(eq(R.string.fragment_projects_item_clock_in_at), anyString())).thenReturn("Clock in %s at given date and time");
        when(resources.getString(R.string.fragment_projects_item_clock_out_at)).thenReturn("Clock out at given date and time");

        when(resources.getString(R.string.fragment_projects_item_clocked_in_since)).thenReturn("Since %s (%s)");

        return resources;
    }
}
