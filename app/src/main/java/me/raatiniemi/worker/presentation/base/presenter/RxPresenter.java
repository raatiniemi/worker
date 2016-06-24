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

package me.raatiniemi.worker.presentation.base.presenter;

import android.content.Context;

import me.raatiniemi.worker.presentation.base.view.MvpView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Presenter with functionality for working with RxJava and observables.
 *
 * @param <V> Reference type for the view attached to the presenter.
 */
public abstract class RxPresenter<V extends MvpView> extends BasePresenter<V> {
    /**
     * Subscription for retrieving data for the presenter/view.
     */
    protected Subscription mSubscription;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    protected RxPresenter(Context context) {
        super(context);
    }

    @Override
    public void detachView() {
        super.detachView();

        // Before we can detach the view, we have to make sure that the
        // subscription have been unsubscribed.
        unsubscribe();
    }

    /**
     * Unsubscribe to the data retrieving subscription.
     */
    protected void unsubscribe() {
        // If have to verify that there is an active subscription.
        if (null != mSubscription && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mSubscription = null;
    }

    /**
     * Apply the default schedulers for the observable.
     *
     * @param <T> Type used with the observable.
     * @return Observable with applied schedulers.
     */
    protected <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
