package me.raatiniemi.worker.base.presenter;

import me.raatiniemi.worker.base.view.MvpView;

/**
 * Interface for the presenter.
 *
 * @param <V> Reference type for the view attached to the presenter.
 */
public interface MvpPresenter<V extends MvpView> {
    /**
     * Attach a view to the presenter.
     *
     * @param view View to attach to the presenter.
     */
    void attachView(V view);

    /**
     * Detach the view from the presenter.
     */
    void detachView();
}
