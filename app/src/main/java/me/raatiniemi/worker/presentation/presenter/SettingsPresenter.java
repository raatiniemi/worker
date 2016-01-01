/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.presentation.presenter;

import android.content.Context;
import android.util.Log;

import java.io.File;

import de.greenrobot.event.EventBus;
import me.raatiniemi.worker.presentation.base.presenter.RxPresenter;
import me.raatiniemi.worker.presentation.model.backup.Backup;
import me.raatiniemi.worker.presentation.model.backup.BackupSuccessfulEvent;
import me.raatiniemi.worker.presentation.view.SettingsView;
import me.raatiniemi.worker.util.ExternalStorage;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

public class SettingsPresenter extends RxPresenter<SettingsView> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "SettingsPresenter";

    private final EventBus mEventBus;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public SettingsPresenter(Context context, EventBus eventBus) {
        super(context);

        mEventBus = eventBus;
    }

    @Override
    public void attachView(SettingsView view) {
        super.attachView(view);

        mEventBus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();

        mEventBus.unregister(this);
    }

    /**
     * Retrieve the latest backup and update the view.
     */
    public void getLatestBackup() {
        Observable.defer(new Func0<Observable<Backup>>() {
            @Override
            public Observable<Backup> call() {
                File directory = ExternalStorage.getLatestBackupDirectory();
                return Observable.just(new Backup(directory));
            }
        }).compose(this.<Backup>applySchedulers())
                .subscribe(new Subscriber<Backup>() {
                    @Override
                    public void onNext(Backup backup) {
                        Log.d(TAG, "getLatestBackup onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing the latest backup");
                            return;
                        }

                        // Push the data to the view.
                        getView().setLatestBackup(backup);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "getLatestBackup onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get latest backup: " + e.getMessage());

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing the latest backup");
                            return;
                        }

                        // Push null as the latest backup to indicate
                        // that an error has occurred.
                        getView().setLatestBackup(null);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "getLatestBackup onCompleted");
                    }
                });
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(BackupSuccessfulEvent event) {
        // Check that we still have the view attached.
        if (!isViewAttached()) {
            Log.d(TAG, "View is not attached, skip pushing the latest backup");
            return;
        }

        // Push the latest backup to the view for update.
        getView().setLatestBackup(event.getBackup());
    }
}
