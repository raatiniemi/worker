package me.raatiniemi.worker.mvp;

public interface MvpPresenter<V extends MvpView> {
    void attachView(V view);

    void detachView();
}
