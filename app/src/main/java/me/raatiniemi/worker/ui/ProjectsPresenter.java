package me.raatiniemi.worker.ui;

import android.app.Fragment;

public class ProjectsPresenter
{
    private Fragment mFragment;

    private Fragment getView()
    {
        return mFragment;
    }

    private boolean isViewAttached()
    {
        return null != getView();
    }

    public void attachView(Fragment fragment)
    {
        mFragment = fragment;
    }

    public void detachView()
    {
        mFragment = null;
    }
}
