package me.raatiniemi.worker.ui;


public class ProjectsPresenter
{
    private ProjectListFragment mFragment;

    private ProjectListFragment getView()
    {
        return mFragment;
    }

    private boolean isViewAttached()
    {
        return null != getView();
    }

    public void attachView(ProjectListFragment fragment)
    {
        mFragment = fragment;
    }

    public void detachView()
    {
        mFragment = null;
    }
}
