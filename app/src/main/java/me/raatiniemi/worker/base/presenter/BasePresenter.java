package me.raatiniemi.worker.base.presenter;

import me.raatiniemi.worker.base.view.MvpView;

/**
 * Base presenter with basic functionality.
 *
 * @param <V> Reference type for the view attached to the presenter.
 */
public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    /**
     * View attached to the presenter.
     */
    private V mView;

    /**
     * Retrieve the view attached to the presenter.
     *
     * @return Attached view if available, otherwise null.
     */
    public V getView() {
        return mView;
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
        mView = view;
    }

    /**
     * Detach the view from the presenter.
     */
    @Override
    public void detachView() {
        mView = null;
    }
}
