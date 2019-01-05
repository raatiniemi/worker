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

package me.raatiniemi.worker.features.projects.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.domain.exception.NoProjectIdException
import me.raatiniemi.worker.domain.interactor.RemoveProject
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import timber.log.Timber

class RemoveProjectViewModel(private val removeProject: RemoveProject) : ViewModel() {
    val restoreProject = ConsumableLiveData<ProjectsItemAdapterResult>()

    suspend fun remove(result: ProjectsItemAdapterResult) = withContext(Dispatchers.IO) {
        try {
            val (_, projectsItem) = result

            removeProject(projectsItem.asProject())
        } catch (e: NoProjectIdException) {
            Timber.w(e, "Unable to remove project without id")
        } catch (e: Exception) {
            restoreProject.postValue(result)
        }
    }
}
