package me.raatiniemi.worker.mvp;

public interface MvpPresenter<V extends MvpView>
{
    public void attachView(V view);

    public void detachView();
}
