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

package me.raatiniemi.worker.base.presenter;

import android.content.Context;

import java.lang.ref.WeakReference;

import me.raatiniemi.worker.base.view.MvpView;

/**
 * Base presenter with basic functionality.
 *
 * @param <V> Reference type for the view attached to the presenter.
 */
abstract public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    /**
     * Context used with the presenter.
     */
    private final Context mContext;

    /**
     * Weak reference for the view attached to the presenter.
     */
    private WeakReference<V> mViewReference;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public BasePresenter(Context context) {
        mContext = context;
    }

    /**
     * Get the context.
     *
     * @return Context used with the presenter.
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * Retrieve the view attached to the presenter.
     *
     * @return Attached view if available, otherwise null.
     */
    public V getView() {
        return null != mViewReference ? mViewReference.get() : null;
    }

    /**
     * Check whether a view is attached to the presenter.
     *
     * @return True if a view is attached, otherwise false.
     */
    public boolean isViewAttached() {
        return null != getView();
    }

    /**
     * Attach a view to the presenter.
     *
     * @param view View to attach to the presenter.
     */
    @Override
    public void attachView(V view) {
        mViewReference = new WeakReference<>(view);
    }

    /**
     * Detach the view from the presenter.
     */
    @Override
    public void detachView() {
        if (null != mViewReference) {
            mViewReference.clear();
            mViewReference = null;
        }
    }
}
