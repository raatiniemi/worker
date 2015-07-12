package me.raatiniemi.worker.settings;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.base.presenter.RxPresenter;
import me.raatiniemi.worker.util.ExternalStorage;
import me.raatiniemi.worker.util.Worker;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

public class SettingsPresenter extends RxPresenter<SettingsActivity> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "SettingsPresenter";

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public SettingsPresenter(Context context) {
        super(context);
    }

    /**
     * Get the backup summary with information for last performed backup.
     */
    public void getBackupSummary() {
        // TODO: Refactor and migrated from the presenter.
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                // Default summary, if no backups have been performed.
                String summary = "No backup have been performed.";

                // Attempt to retrieve the latest backup directory.
                File directory = ExternalStorage.getLatestBackupDirectory();
                if (null != directory) {
                    // Retrieve the timestamp from the backup directory name.
                    String timestamp = directory.getName().replaceFirst(
                        Worker.STORAGE_BACKUP_DIRECTORY_PATTERN,
                        "$1"
                    );

                    Date date = new Date(Long.valueOf(timestamp));

                    // Build the summary with the formatted date.
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    summary = "Last backup was performed at " + formatter.format(date) + ".";
                }

                return Observable.just(summary);
            }
        }).compose(this.<String>applySchedulers())
            .subscribe(new Subscriber<String>() {
                @Override
                public void onNext(String summary) {
                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing backup summary");
                        return;
                    }

                    // Push the summary to the Backup-preference.
                    getView().setBackupSummary(summary);
                }

                @Override
                public void onError(Throwable e) {
                    // Something has gone wrong when fetching the backup summary.
                    // We'd want to log the failure, even if the view is detached.
                    Log.w(TAG, "Failed to get backup summary: " + e.getMessage());

                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing backup summary");
                        return;
                    }

                    // Push an error message as the Backup-preference summary.
                    getView().setBackupSummary("Unable to retrieve when last backup was performed.");
                }

                @Override
                public void onCompleted() {
                    Log.d(TAG, "onCompleted for getBackupSummary was reached");
                }
            });
    }

    /**
     * Get the restore summary with information for last performed backup.
     */
    public void getRestoreSummary() {
        // TODO: Refactor and migrated from the presenter.
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                String summary = null;

                // Attempt to retrieve the latest backup directory.
                File directory = ExternalStorage.getLatestBackupDirectory();
                if (null != directory) {
                    // Retrieve the timestamp from the backup directory name.
                    String timestamp = directory.getName().replaceFirst(
                        Worker.STORAGE_BACKUP_DIRECTORY_PATTERN,
                        "$1"
                    );

                    Date date = new Date(Long.valueOf(timestamp));

                    // Build the summary with the formatted date.
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    summary = "Restore backup from " + formatter.format(date) + ".";
                }

                return Observable.just(summary);
            }
        }).compose(this.<String>applySchedulers())
            .subscribe(new Subscriber<String>() {
                @Override
                public void onNext(String summary) {
                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing restore summary");
                        return;
                    }

                    boolean enable = true;
                    if (null == summary) {
                        summary = "Nothing to restore, no backup have been performed.";
                        enable = false;
                    }

                    // Push the summary to the Restore-preference.
                    getView().setRestoreSummary(summary, enable);
                }

                @Override
                public void onError(Throwable e) {
                    // Something has gone wrong when fetching the restore summary.
                    // We'd want to log the failure, even if the view is detached.
                    Log.w(TAG, "Failed to get restore summary: " + e.getMessage());

                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing restore summary");
                        return;
                    }

                    // Push an error message as the Restore-preference summary.
                    getView().setRestoreSummary("Unable to restore, unable to find backup.", false);
                }

                @Override
                public void onCompleted() {
                    Log.d(TAG, "onCompleted for getRestoreSummary was reached");
                }
            });
    }
}
