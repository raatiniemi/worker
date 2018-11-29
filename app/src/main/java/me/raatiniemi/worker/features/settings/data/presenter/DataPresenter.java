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

package me.raatiniemi.worker.features.settings.data.presenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import me.raatiniemi.worker.data.util.ExternalStorage;
import me.raatiniemi.worker.features.settings.data.model.Backup;
import me.raatiniemi.worker.features.settings.data.model.BackupSuccessfulEvent;
import me.raatiniemi.worker.features.settings.data.view.DataView;
import me.raatiniemi.worker.features.shared.presenter.BasePresenter;
import me.raatiniemi.worker.util.RxUtil;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

import static me.raatiniemi.worker.util.RxUtil.unsubscribeIfNotNull;

public class DataPresenter extends BasePresenter<DataView> {
    private Subscription getLatestBackupSubscription;

    private final EventBus eventBus;

    public DataPresenter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void attachView(DataView view) {
        super.attachView(view);

        eventBus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();

        eventBus.unregister(this);
        unsubscribeIfNotNull(getLatestBackupSubscription);
    }

    /**
     * Retrieve the latest backup and update the view.
     */
    public void getLatestBackup() {
        unsubscribeIfNotNull(getLatestBackupSubscription);

        getLatestBackupSubscription = Observable
                .defer(() -> {
                    File directory = ExternalStorage.getLatestBackupDirectory();
                    return Observable.just(new Backup(directory));
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<Backup>() {
                    @Override
                    public void onNext(Backup backup) {
                        Timber.d("getLatestBackup onNext");

                        performWithView(view -> view.setLatestBackup(backup));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("getLatestBackup onError");

                        // Log the error even if the view have been detached.
                        Timber.w(e, "Failed to get latest backup");

                        performWithView(view -> view.setLatestBackup(null));
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("getLatestBackup onCompleted");
                    }
                });
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BackupSuccessfulEvent event) {
        performWithView(view -> view.setLatestBackup(event.getBackup()));
    }
}
