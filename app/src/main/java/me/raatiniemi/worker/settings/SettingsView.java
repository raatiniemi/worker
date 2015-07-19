package me.raatiniemi.worker.settings;

import android.support.annotation.Nullable;

import me.raatiniemi.worker.base.view.MvpView;
import me.raatiniemi.worker.model.backup.Backup;

/**
 * Methods related to handling of the settings view.
 */
public interface SettingsView extends MvpView {
    /**
     * Update the view with the latest backup.
     *
     * @param backup Latest backup.
     */
    void setLatestBackup(@Nullable Backup backup);
}
