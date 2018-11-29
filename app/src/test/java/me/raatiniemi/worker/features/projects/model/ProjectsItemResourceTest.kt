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

package me.raatiniemi.worker.features.projects.model

import android.content.res.Resources

import me.raatiniemi.worker.R

import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

abstract class ProjectsItemResourceTest {
    val resources: Resources
        get() {
            val resources = mock(Resources::class.java)
            `when`(resources.getString(eq(R.string.fragment_projects_item_clock_in), anyString())).thenReturn("Clock in %s")
            `when`(resources.getString(eq(R.string.fragment_projects_item_clock_out), anyString())).thenReturn("Clock out %s")

            `when`(resources.getString(eq(R.string.fragment_projects_item_clock_in_at), anyString())).thenReturn("Clock in %s at")
            `when`(resources.getString(eq(R.string.fragment_projects_item_clock_out_at), anyString())).thenReturn("Clock out %s at")

            `when`(resources.getString(eq(R.string.fragment_projects_item_delete), anyString())).thenReturn("Delete %s")

            `when`(resources.getString(R.string.fragment_projects_item_clocked_in_since)).thenReturn("Since %s (%s)")

            return resources
        }
}
