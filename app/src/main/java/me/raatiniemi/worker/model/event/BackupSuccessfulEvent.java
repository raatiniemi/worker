package me.raatiniemi.worker.model.event;

import android.support.annotation.NonNull;

import me.raatiniemi.worker.model.backup.Backup;

public class BackupSuccessfulEvent {
    private Backup mBackup;

    public BackupSuccessfulEvent(@NonNull Backup backup) {
        mBackup = backup;
    }

    @NonNull
    public Backup getBackup() {
        return mBackup;
    }
}
