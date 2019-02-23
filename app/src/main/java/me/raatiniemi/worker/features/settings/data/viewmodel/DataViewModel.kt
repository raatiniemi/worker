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

package me.raatiniemi.worker.features.settings.data.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.data.util.ExternalStorage
import me.raatiniemi.worker.features.settings.data.model.Backup
import me.raatiniemi.worker.features.settings.data.model.DataViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import timber.log.Timber

class DataViewModel : ViewModel() {
    val viewActions = ConsumableLiveData<DataViewActions>()

    suspend fun getLatestBackup() = withContext(Dispatchers.IO) {
        try {
            val directory = ExternalStorage.getLatestBackupDirectory()
            if (directory == null) {
                viewActions.postValue(DataViewActions.NoBackupIsAvailable)
                return@withContext
            }

            val backup = Backup(directory)
            viewActions.postValue(DataViewActions.LatestBackup(backup))
        } catch (e: Exception) {
            Timber.d(e, "Unable to find latest backup")
            viewActions.postValue(DataViewActions.UnableToFindBackup)
        }
    }
}
