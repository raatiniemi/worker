/*
 * Copyright (C) 2015-2016 Worker Project
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import me.raatiniemi.worker.data.util.ExternalStorage;
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.presentation.base.presenter.RxPresenter;
import me.raatiniemi.worker.presentation.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.model.backup.Backup;
import me.raatiniemi.worker.presentation.model.backup.BackupSuccessfulEvent;
import me.raatiniemi.worker.presentation.util.Settings;
import me.raatiniemi.worker.presentation.view.SettingsView;
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
                        Log.w(TAG, "Failed to get latest backup", e);

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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BackupSuccessfulEvent event) {
        // Check that we still have the view attached.
        if (!isViewAttached()) {
            Log.d(TAG, "View is not attached, skip pushing the latest backup");
            return;
        }

        // Push the latest backup to the view for update.
        getView().setLatestBackup(event.getBackup());
    }

    public void changeTimeSummaryStartingPoint(int newStartingPoint) {
        try {
            switch (newStartingPoint) {
                case GetProjectTimeSince.sWeek:
                    Settings.useWeekForTimeSummaryStartingPoint(getContext());
                    break;
                case GetProjectTimeSince.sMonth:
                    Settings.useMonthForTimeSummaryStartingPoint(getContext());
                    break;
                default:
                    throw new InvalidStartingPointException(
                            "Starting point '" + newStartingPoint + "' is not valid"
                    );
            }

            mEventBus.post(new TimeSummaryStartingPointChangeEvent());

            if (!isViewAttached()) {
                return;
            }
            getView().showChangeTimeSummaryStartingPointSuccessMessage();
        } catch (InvalidStartingPointException e) {
            Log.w(TAG, "Unable to set new starting point", e);

            if (!isViewAttached()) {
                Log.w(TAG, "View is not attached, failed to push error");
                return;
            }
            getView().showChangeTimeSummaryStartingPointErrorMessage();
        }
    }
}
