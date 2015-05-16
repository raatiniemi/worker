package me.raatiniemi.worker.base.presenter;

import me.raatiniemi.worker.base.view.MvpView;

public interface MvpPresenter<V extends MvpView> {
    void attachView(V view);

    void detachView();
}
