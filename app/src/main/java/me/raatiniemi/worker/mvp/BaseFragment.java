package me.raatiniemi.worker.mvp;

import android.app.Fragment;

abstract public class BaseFragment<P extends BasePresenter> extends Fragment implements MvpView
{
    private P mPresenter;

    abstract protected P createPresenter();

    protected P getPresenter()
    {
        if (null == mPresenter) {
            mPresenter = createPresenter();
        }
        return mPresenter;
    }
}
