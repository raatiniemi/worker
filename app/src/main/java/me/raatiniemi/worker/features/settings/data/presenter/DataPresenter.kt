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

package me.raatiniemi.worker.features.settings.data.presenter

import me.raatiniemi.worker.data.util.ExternalStorage
import me.raatiniemi.worker.features.settings.data.model.Backup
import me.raatiniemi.worker.features.settings.data.model.BackupSuccessfulEvent
import me.raatiniemi.worker.features.settings.data.view.DataView
import me.raatiniemi.worker.features.shared.presenter.BasePresenter
import me.raatiniemi.worker.util.RxUtil
import me.raatiniemi.worker.util.RxUtil.unsubscribeIfNotNull
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rx.Observable
import rx.Subscriber
import rx.Subscription
import timber.log.Timber

class DataPresenter(private val eventBus: EventBus) : BasePresenter<DataView>() {
    private var getLatestBackupSubscription: Subscription? = null

    override fun attachView(view: DataView) {
        super.attachView(view)

        eventBus.register(this)
    }

    override fun detachView() {
        super.detachView()

        eventBus.unregister(this)
        unsubscribeIfNotNull(getLatestBackupSubscription)
    }

    /**
     * Retrieve the latest backup and update the view.
     */
    fun getLatestBackup() {
        unsubscribeIfNotNull(getLatestBackupSubscription)

        getLatestBackupSubscription = Observable
                .defer {
                    val directory = ExternalStorage.getLatestBackupDirectory()
                    Observable.just(Backup(directory))
                }
                .compose(RxUtil.applySchedulers())
                .subscribe(object : Subscriber<Backup>() {
                    override fun onNext(backup: Backup) {
                        Timber.d("getLatestBackup onNext")

                        performWithView { view -> view.setLatestBackup(backup) }
                    }

                    override fun onError(e: Throwable) {
                        Timber.d("getLatestBackup onError")

                        // Log the error even if the view have been detached.
                        Timber.w(e, "Failed to get latest backup")

                        performWithView { view -> view.setLatestBackup(null) }
                    }

                    override fun onCompleted() {
                        Timber.d("getLatestBackup onCompleted")
                    }
                })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: BackupSuccessfulEvent) {
        performWithView { view -> view.setLatestBackup(event.backup) }
    }
}
