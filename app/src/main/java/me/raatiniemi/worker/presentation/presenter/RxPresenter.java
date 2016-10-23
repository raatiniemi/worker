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

import me.raatiniemi.worker.presentation.view.MvpView;
import rx.Subscription;

import static me.raatiniemi.worker.presentation.util.RxUtil.unsubscribeIfNotNull;

/**
 * Presenter with functionality for working with RxJava and observables.
 *
 * @param <V> Reference type for the view attached to the presenter.
 */
public abstract class RxPresenter<V extends MvpView> extends BasePresenter<V> {
    /**
     * Subscription for retrieving data for the presenter/view.
     */
    protected Subscription subscription;

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
        unsubscribeIfNotNull(subscription);
    }
}
