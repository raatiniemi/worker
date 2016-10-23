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

package me.raatiniemi.worker.presentation.settings.presenter;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import me.raatiniemi.worker.data.util.ExternalStorage;
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.settings.model.Backup;
import me.raatiniemi.worker.presentation.settings.model.BackupSuccessfulEvent;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.settings.view.SettingsView;
import me.raatiniemi.worker.presentation.util.RxUtil;
import me.raatiniemi.worker.presentation.util.Settings;
import rx.Observable;
import rx.Subscriber;

public class SettingsPresenter extends BasePresenter<SettingsView> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "SettingsPresenter";

    private final EventBus eventBus;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public SettingsPresenter(Context context, EventBus eventBus) {
        super(context);

        this.eventBus = eventBus;
    }

    @Override
    public void attachView(SettingsView view) {
        super.attachView(view);

        eventBus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();

        eventBus.unregister(this);
    }

    /**
     * Retrieve the latest backup and update the view.
     */
    public void getLatestBackup() {
        Observable
                .defer(() -> {
                    File directory = ExternalStorage.getLatestBackupDirectory();
                    return Observable.just(new Backup(directory));
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<Backup>() {
                    @Override
                    public void onNext(Backup backup) {
                        Log.d(TAG, "getLatestBackup onNext");

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
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
                        if (isViewDetached()) {
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
        if (isViewDetached()) {
            Log.d(TAG, "View is not attached, skip pushing the latest backup");
            return;
        }

        // Push the latest backup to the view for update.
        getView().setLatestBackup(event.getBackup());
    }

    public void changeTimeSummaryStartingPoint(int newStartingPoint) {
        try {
            int currentStartingPoint = Settings.getStartingPointForTimeSummary(getContext());
            if (currentStartingPoint == newStartingPoint) {
                return;
            }

            switch (newStartingPoint) {
                case GetProjectTimeSince.WEEK:
                    Settings.useWeekForTimeSummaryStartingPoint(getContext());
                    break;
                case GetProjectTimeSince.MONTH:
                    Settings.useMonthForTimeSummaryStartingPoint(getContext());
                    break;
                default:
                    throw new InvalidStartingPointException(
                            "Starting point '" + newStartingPoint + "' is not valid"
                    );
            }

            eventBus.post(new TimeSummaryStartingPointChangeEvent());

            if (isViewDetached()) {
                return;
            }
            if (GetProjectTimeSince.WEEK == newStartingPoint) {
                getView().showChangeTimeSummaryStartingPointToWeekSuccessMessage();
                return;
            }
            getView().showChangeTimeSummaryStartingPointToMonthSuccessMessage();
        } catch (InvalidStartingPointException e) {
            Log.w(TAG, "Unable to set new starting point", e);

            if (isViewDetached()) {
                Log.w(TAG, "View is not attached, failed to push error");
                return;
            }
            getView().showChangeTimeSummaryStartingPointErrorMessage();
        }
    }
}
