/*
 * Copyright (C) 2017 Worker Project
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

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;

import me.raatiniemi.worker.presentation.view.MvpView;
import timber.log.Timber;

import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.util.NullUtil.nonNull;

/**
 * Base presenter with basic functionality.
 *
 * @param <V> Reference type for the view attached to the presenter.
 */
public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    /**
     * Weak reference for the view attached to the presenter.
     */
    private WeakReference<V> viewReference;

    /**
     * Retrieve the view attached to the presenter.
     *
     * @return Attached view if available, otherwise null.
     */
    private V getView() {
        return nonNull(viewReference) ? viewReference.get() : null;
    }

    /**
     * Perform an action with the view.
     *
     * @param viewAction Action to perform with the view.
     */
    @UiThread
    protected void performWithView(@NonNull ViewAction<V> viewAction) {
        V view = getView();
        if (isNull(view)) {
            Timber.d("View is not attached");
            return;
        }

        viewAction.perform(view);
    }

    /**
     * Attach a view to the presenter.
     *
     * @param view View to attach to the presenter.
     */
    @CallSuper
    @Override
    public void attachView(V view) {
        this.viewReference = new WeakReference<>(view);
    }

    /**
     * Detach the view from the presenter.
     */
    @CallSuper
    @Override
    public void detachView() {
        if (nonNull(viewReference)) {
            viewReference.clear();
            viewReference = null;
        }
    }
}
