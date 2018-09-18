/*
 * Copyright (C) 2018 Worker Project
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

package me.raatiniemi.worker.features.settings.data.view;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.service.data.BackupService;
import me.raatiniemi.worker.data.service.data.RestoreService;
import me.raatiniemi.worker.features.settings.Presenters;
import me.raatiniemi.worker.features.settings.data.model.Backup;
import me.raatiniemi.worker.features.settings.data.presenter.DataPresenter;
import me.raatiniemi.worker.features.settings.view.BasePreferenceFragment;
import me.raatiniemi.worker.features.shared.view.dialog.RxAlertDialog;
import me.raatiniemi.worker.util.PermissionUtil;
import timber.log.Timber;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;
import static me.raatiniemi.worker.util.PresenterUtil.detachViewIfNotNull;

public class DataFragment extends BasePreferenceFragment
        implements DataView, ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * Key for the data backup preference.
     */
    private static final String SETTINGS_DATA_BACKUP_KEY = "settings_data_backup";

    /**
     * Key for the data restore preference.
     */
    private static final String SETTINGS_DATA_RESTORE_KEY = "settings_data_restore";

    /**
     * Code for requesting permission for reading external storage.
     */
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * Code for requesting permission for writing to external storage.
     */
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.forLanguageTag("en_US"));

    private Snackbar snackbar;

    private final Presenters presenters = new Presenters();
    private final DataPresenter presenter = presenters.getData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter.attachView(this);

        addPreferencesFromResource(R.xml.settings_data);

        // Check for the latest backup.
        checkLatestBackup();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (nonNull(snackbar)) {
            snackbar.dismiss();
        }

        detachViewIfNotNull(presenter);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                // Whether we've been granted read permission or not, a call to
                // the `checkLatestBackup` will handle both scenarios.
                checkLatestBackup();
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                // Only if we've been granted write permission should we backup.
                // We should not display the permission message again unless the
                // user attempt to backup.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    runBackup();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
        // Check if we support the user action, if not, send it to the
        // parent which will handle it.
        switch (preference.getKey()) {
            case SETTINGS_DATA_BACKUP_KEY:
                runBackup();
                break;
            case SETTINGS_DATA_RESTORE_KEY:
                runRestore();
                break;
            default:
                return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return false;
    }

    @Override
    public int getTitle() {
        return R.string.activity_settings_data;
    }

    /**
     * Initiate the backup action.
     */
    private void runBackup() {
        // We should only attempt to backup if permission to write
        // to the external storage have been granted.
        if (PermissionUtil.havePermission(getActivity(), WRITE_EXTERNAL_STORAGE)) {
            Timber.d("Permission for writing to external storage is granted");
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.message_backing_up_data,
                    Snackbar.LENGTH_SHORT
            ).show();

            BackupService.startBackup(getActivity());
            return;
        }

        // We have not been granted permission to write to the external storage. Display
        // the permission message and allow the user to initiate the permission request.
        Timber.d("Permission for writing to external storage is not granted");
        requestPermission(
                R.string.message_permission_write_backup,
                new String[]{WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE
        );
    }

    private void requestPermission(
            @StringRes int message,
            String[] permissions,
            @IntRange(from = 0) int requestCode
    ) {
        View contentView = getActivity().findViewById(android.R.id.content);

        snackbar = Snackbar.make(contentView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(
                        android.R.string.ok,
                        view -> ActivityCompat.requestPermissions(
                                getActivity(),
                                permissions,
                                requestCode
                        )
                );
        snackbar.show();
    }

    /**
     * Initiate the restore action.
     */
    private void runRestore() {
        ConfirmRestoreDialog.show(getActivity())
                .filter(RxAlertDialog::isPositive)
                .subscribe(
                        which -> {
                            Snackbar.make(
                                    getActivity().findViewById(android.R.id.content),
                                    R.string.message_restoring_data,
                                    Snackbar.LENGTH_SHORT
                            ).show();

                            RestoreService.startRestore(getActivity());
                        },
                        Timber::w
                );
    }

    /**
     * Get the latest backup, if permission have been granted.
     */
    private void checkLatestBackup() {
        // We should only attempt to check the latest backup if permission
        // to read the external storage have been granted.
        if (PermissionUtil.havePermission(getActivity(), READ_EXTERNAL_STORAGE)) {
            // Tell the SettingsActivity to fetch the latest backup.
            Timber.d("Permission for reading external storage is granted");
            presenter.getLatestBackup();

            // No need to go any further.
            return;
        }

        // We have not been granted permission to read the external storage. Display the
        // permission message and allow the user to initiate the permission request.
        Timber.d("Permission for reading external storage is not granted");
        requestPermission(
                R.string.message_permission_read_backup,
                new String[]{READ_EXTERNAL_STORAGE},
                REQUEST_READ_EXTERNAL_STORAGE
        );
    }

    @Override
    public void setLatestBackup(@Nullable Backup backup) {
        setBackupSummary(backup);
        setRestoreSummary(backup);
    }

    /**
     * Set the backup summary based on the latest backup.
     *
     * @param backup Latest available backup.
     */
    private void setBackupSummary(@Nullable Backup backup) {
        Preference preference = findPreference(SETTINGS_DATA_BACKUP_KEY);
        if (isNull(preference)) {
            Timber.w("Unable to find preference with key: %s", SETTINGS_DATA_BACKUP_KEY);
            return;
        }

        String text = getString(R.string.activity_settings_backup_unable_to_find);
        if (nonNull(backup)) {
            text = getString(R.string.activity_settings_backup_none_available);

            Date date = backup.getDate();
            if (nonNull(date)) {
                text = getString(
                        R.string.activity_settings_backup_performed_at,
                        format.format(date)
                );
            }
        }

        preference.setSummary(text);
    }

    /**
     * Set the restore summary based on the latest backup.
     *
     * @param backup Latest available backup.
     */
    private void setRestoreSummary(@Nullable Backup backup) {
        Preference preference = findPreference(SETTINGS_DATA_RESTORE_KEY);
        if (isNull(preference)) {
            Timber.w("Unable to find preference with key: %s", SETTINGS_DATA_RESTORE_KEY);
            return;
        }

        String text = getString(R.string.activity_settings_restore_unable_to_find);
        boolean enable = false;
        if (nonNull(backup)) {
            text = getString(R.string.activity_settings_restore_none_available);

            Date date = backup.getDate();
            if (nonNull(date)) {
                text = getString(
                        R.string.activity_settings_restore_from,
                        format.format(date)
                );
                enable = true;
            }
        }

        preference.setSummary(text);
        preference.setEnabled(enable);
    }
}
