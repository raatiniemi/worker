package me.raatiniemi.worker.base.presenter;

import me.raatiniemi.worker.mvp.MvpView;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    private V mView;

    protected V getView() {
        return mView;
    }

    protected boolean isViewAttached() {
        return null != getView();
    }

    @Override
    public void attachView(V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }
}
