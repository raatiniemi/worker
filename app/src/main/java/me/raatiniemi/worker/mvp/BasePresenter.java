package me.raatiniemi.worker.mvp;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V>
{
    @Override
    public void attachView(V view)
    {
    }

    @Override
    public void detachView()
    {
    }
}
