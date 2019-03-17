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

package me.raatiniemi.worker.features.projects.all.view

import me.raatiniemi.worker.features.projects.all.model.ProjectsItem
import me.raatiniemi.worker.features.shared.view.fragment.DateTimePickerFragment
import java.util.*

internal class ClockActivityAtFragment : DateTimePickerFragment(), DateTimePickerFragment.OnDateTimeSetListener {
    private lateinit var onDateTimeSetListener: (Calendar) -> Unit

    init {
        setOnDateTimeSetListener(this)
    }

    override fun onDateTimeSet(calendar: Calendar) {
        onDateTimeSetListener(calendar)
    }

    companion object {
        fun newInstance(projectsItem: ProjectsItem, onDateTimeSet: (Calendar) -> Unit): ClockActivityAtFragment {
            val fragment = ClockActivityAtFragment()
            fragment.onDateTimeSetListener = onDateTimeSet

            if (projectsItem.isActive) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = projectsItem.clockedInSinceInMilliseconds
                }

                fragment.setMinDate(calendar)
            }

            return fragment
        }
    }
}
