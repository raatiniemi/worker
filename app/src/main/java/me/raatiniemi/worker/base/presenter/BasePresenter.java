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
    private Context mContext;

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
