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

package me.raatiniemi.worker.features.settings.data.view

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.view.View
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.data.service.data.BackupService
import me.raatiniemi.worker.data.service.data.RestoreService
import me.raatiniemi.worker.features.settings.data.model.BackupSuccessfulEvent
import me.raatiniemi.worker.features.settings.data.model.DataViewActions
import me.raatiniemi.worker.features.settings.data.viewmodel.DataViewModel
import me.raatiniemi.worker.features.settings.view.BasePreferenceFragment
import me.raatiniemi.worker.features.shared.view.ConfirmAction
import me.raatiniemi.worker.features.shared.view.configurePreference
import me.raatiniemi.worker.util.PermissionUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class DataFragment : BasePreferenceFragment(), CoroutineScope, ActivityCompat.OnRequestPermissionsResultCallback {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    private val eventBus = EventBus.getDefault()

    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.forLanguageTag("en_US"))

    private val vm: DataViewModel by viewModel()

    private var snackBar: Snackbar? = null

    override val title = R.string.activity_settings_data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)
        checkLatestBackup()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.viewActions.observeAndConsume(this, Observer {
            configureView(it)
        })
    }

    private fun configureView(action: DataViewActions) {
        configureBackupPreference(action)
        configureRestorePreference(action)
    }

    private fun configureBackupPreference(action: DataViewActions) {
        configurePreference<Preference>(SETTINGS_DATA_BACKUP_KEY) {
            summary = when (action) {
                is DataViewActions.UnableToFindBackup ->
                    getString(R.string.activity_settings_backup_unable_to_find)

                is DataViewActions.NoBackupIsAvailable ->
                    getString(R.string.activity_settings_backup_none_available)

                is DataViewActions.LatestBackup -> getString(
                        R.string.activity_settings_backup_performed_at,
                        format.format(action.backup.date)
                )
            }
        }
    }

    private fun configureRestorePreference(action: DataViewActions) {
        configurePreference<Preference>(SETTINGS_DATA_RESTORE_KEY) {
            summary = when (action) {
                is DataViewActions.UnableToFindBackup ->
                    getString(R.string.activity_settings_restore_unable_to_find)

                is DataViewActions.NoBackupIsAvailable ->
                    getString(R.string.activity_settings_restore_none_available)

                is DataViewActions.LatestBackup -> getString(
                        R.string.activity_settings_restore_from,
                        format.format(action.backup.date)
                )
            }
            isEnabled = action is DataViewActions.LatestBackup
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        snackBar?.dismiss()
        eventBus.unregister(this)

        coroutineContext.cancel()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE ->
                // Whether we've been granted read permission or not, a call to
                // the `checkLatestBackup` will handle both scenarios.
                checkLatestBackup()
            REQUEST_WRITE_EXTERNAL_STORAGE ->
                // Only if we've been granted write permission should we backup.
                // We should not display the permission message again unless the
                // user attempt to backup.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    runBackup()
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        // Check if we support the user action, if not, send it to the
        // parent which will handle it.
        when (preference?.key) {
            SETTINGS_DATA_BACKUP_KEY -> runBackup()
            SETTINGS_DATA_RESTORE_KEY -> runRestore()
            else -> return super.onPreferenceTreeClick(preference)
        }

        return false
    }

    /**
     * Initiate the backup action.
     */
    private fun runBackup() {
        // We should only attempt to backup if permission to write
        // to the external storage have been granted.
        if (PermissionUtil.havePermission(requireContext(), WRITE_EXTERNAL_STORAGE)) {
            Timber.d("Permission for writing to external storage is granted")
            val snackBar = Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    R.string.message_backing_up_data,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()

            BackupService.startBackup(activity)
            return
        }

        // We have not been granted permission to write to the external storage. Display
        // the permission message and allow the user to initiate the permission request.
        Timber.d("Permission for writing to external storage is not granted")
        requestPermission(
                R.string.message_permission_write_backup,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE
        )
    }

    private fun requestPermission(
            @StringRes message: Int,
            permissions: Array<String>,
            @IntRange(from = 0) requestCode: Int
    ) {
        val contentView = requireActivity().findViewById<View>(android.R.id.content)

        snackBar = Snackbar.make(contentView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(
                        android.R.string.ok
                ) {
                    ActivityCompat.requestPermissions(
                            requireActivity(),
                            permissions,
                            requestCode
                    )
                }
        snackBar?.show()
    }

    /**
     * Initiate the restore action.
     */
    private fun runRestore() = launch {
        val confirmAction = ConfirmRestoreDialog.show(requireContext())
        if (ConfirmAction.YES == confirmAction) {
            val snackBar = Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    R.string.message_restoring_data,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()

            RestoreService.startRestore(activity)
        }
    }

    /**
     * Get the latest backup, if permission have been granted.
     */
    private fun checkLatestBackup() {
        // We should only attempt to check the latest backup if permission
        // to read the external storage have been granted.
        if (PermissionUtil.havePermission(requireContext(), READ_EXTERNAL_STORAGE)) {
            // Tell the SettingsActivity to fetch the latest backup.
            Timber.d("Permission for reading external storage is granted")
            launch {
                vm.getLatestBackup()
            }

            // No need to go any further.
            return
        }

        // We have not been granted permission to read the external storage. Display the
        // permission message and allow the user to initiate the permission request.
        Timber.d("Permission for reading external storage is not granted")
        requestPermission(
                R.string.message_permission_read_backup,
                arrayOf(READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: BackupSuccessfulEvent) {
        configureView(DataViewActions.LatestBackup(event.backup))
    }

    companion object {
        /**
         * Key for the data backup preference.
         */
        private const val SETTINGS_DATA_BACKUP_KEY = "settings_data_backup"

        /**
         * Key for the data restore preference.
         */
        private const val SETTINGS_DATA_RESTORE_KEY = "settings_data_restore"

        /**
         * Code for requesting permission for reading external storage.
         */
        private const val REQUEST_READ_EXTERNAL_STORAGE = 1

        /**
         * Code for requesting permission for writing to external storage.
         */
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 2
    }
}
