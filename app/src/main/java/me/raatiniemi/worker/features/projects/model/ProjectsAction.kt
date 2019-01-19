/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

internal sealed class ProjectsAction(val result: ProjectsItemAdapterResult) {
    class Open(result: ProjectsItemAdapterResult) : ProjectsAction(result)
    class Toggle(result: ProjectsItemAdapterResult) : ProjectsAction(result)
    class At(result: ProjectsItemAdapterResult) : ProjectsAction(result)
    class Remove(result: ProjectsItemAdapterResult) : ProjectsAction(result)
}
