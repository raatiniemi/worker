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
import android.preference.Preference
import android.preference.PreferenceScreen
import android.view.View
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.data.service.data.BackupService
import me.raatiniemi.worker.data.service.data.RestoreService
import me.raatiniemi.worker.features.settings.data.model.Backup
import me.raatiniemi.worker.features.settings.data.presenter.DataPresenter
import me.raatiniemi.worker.features.settings.view.BasePreferenceFragment
import me.raatiniemi.worker.features.shared.view.dialog.RxAlertDialog
import me.raatiniemi.worker.util.NullUtil.isNull
import me.raatiniemi.worker.util.PermissionUtil
import me.raatiniemi.worker.util.PresenterUtil.detachViewIfNotNull
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class DataFragment : BasePreferenceFragment(), DataView, ActivityCompat.OnRequestPermissionsResultCallback {
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.forLanguageTag("en_US"))

    private var snackBar: Snackbar? = null

    private val presenter: DataPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.attachView(this)

        addPreferencesFromResource(R.xml.settings_data)

        // Check for the latest backup.
        checkLatestBackup()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        snackBar?.dismiss()
        detachViewIfNotNull(presenter)
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

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
        // Check if we support the user action, if not, send it to the
        // parent which will handle it.
        when (preference.key) {
            SETTINGS_DATA_BACKUP_KEY -> runBackup()
            SETTINGS_DATA_RESTORE_KEY -> runRestore()
            else -> return super.onPreferenceTreeClick(preferenceScreen, preference)
        }

        return false
    }

    public override fun getTitle() = R.string.activity_settings_data

    /**
     * Initiate the backup action.
     */
    private fun runBackup() {
        // We should only attempt to backup if permission to write
        // to the external storage have been granted.
        if (PermissionUtil.havePermission(activity, WRITE_EXTERNAL_STORAGE)) {
            Timber.d("Permission for writing to external storage is granted")
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
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
        val contentView = activity.findViewById<View>(android.R.id.content)

        snackBar = Snackbar.make(contentView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(
                        android.R.string.ok
                ) {
                    ActivityCompat.requestPermissions(
                            activity,
                            permissions,
                            requestCode
                    )
                }
        snackBar?.show()
    }

    /**
     * Initiate the restore action.
     */
    private fun runRestore() {
        ConfirmRestoreDialog.show(activity)
                .filter { RxAlertDialog.isPositive(it) }
                .subscribe(
                        {
                            val snackBar = Snackbar.make(
                                    activity.findViewById(android.R.id.content),
                                    R.string.message_restoring_data,
                                    Snackbar.LENGTH_SHORT
                            )
                            snackBar.show()

                            RestoreService.startRestore(activity)
                        },
                        { Timber.w(it) }
                )
    }

    /**
     * Get the latest backup, if permission have been granted.
     */
    private fun checkLatestBackup() {
        // We should only attempt to check the latest backup if permission
        // to read the external storage have been granted.
        if (PermissionUtil.havePermission(activity, READ_EXTERNAL_STORAGE)) {
            // Tell the SettingsActivity to fetch the latest backup.
            Timber.d("Permission for reading external storage is granted")
            presenter.getLatestBackup()

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

    override fun setLatestBackup(backup: Backup?) {
        setBackupSummary(backup)
        setRestoreSummary(backup)
    }

    /**
     * Set the backup summary based on the latest backup.
     *
     * @param backup Latest available backup.
     */
    private fun setBackupSummary(backup: Backup?) {
        val preference = findPreference(SETTINGS_DATA_BACKUP_KEY)
        if (isNull(preference)) {
            Timber.w("Unable to find preference with key: %s", SETTINGS_DATA_BACKUP_KEY)
            return
        }

        if (backup == null) {
            preference.summary = getString(R.string.activity_settings_backup_unable_to_find)
            return
        }

        if (backup.date == null) {
            preference.summary = getString(R.string.activity_settings_backup_none_available)
            return
        }

        preference.summary = getString(
                R.string.activity_settings_backup_performed_at,
                format.format(backup.date)
        )
    }

    /**
     * Set the restore summary based on the latest backup.
     *
     * @param backup Latest available backup.
     */
    private fun setRestoreSummary(backup: Backup?) {
        val preference = findPreference(SETTINGS_DATA_RESTORE_KEY)
        if (isNull(preference)) {
            Timber.w("Unable to find preference with key: %s", SETTINGS_DATA_RESTORE_KEY)
            return
        }

        var text = getString(R.string.activity_settings_restore_unable_to_find)
        var enable = false
        if (backup != null) {
            text = getString(R.string.activity_settings_restore_none_available)

            val date = backup.date
            if (date != null) {
                text = getString(
                        R.string.activity_settings_restore_from,
                        format.format(date)
                )
                enable = true
            }
        }

        preference.summary = text
        preference.isEnabled = enable
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
