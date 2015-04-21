package me.raatiniemi.worker.ui;

import android.os.AsyncTask;

import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mvp.BasePresenter;
import me.raatiniemi.worker.util.ProjectCollection;

public class ProjectsPresenter extends BasePresenter<ProjectListFragment>
{
    private ProjectListFragment mFragment;

    private AsyncTask<Void, Void, ProjectCollection> mProjectLoader;

    private ProjectListFragment getView()
    {
        return mFragment;
    }

    private boolean isViewAttached()
    {
        return null != getView();
    }

    @Override
    public void attachView(ProjectListFragment fragment)
    {
        mFragment = fragment;
    }

    @Override
    public void detachView()
    {
        mFragment = null;


        if (null != mProjectLoader && !mProjectLoader.isCancelled()) {
            mProjectLoader.cancel(true);
        }
    }

    public void loadProjects()
    {
        if (null != mProjectLoader && !mProjectLoader.isCancelled()) {
            mProjectLoader.cancel(true);
        }

        mProjectLoader = new AsyncTask<Void, Void, ProjectCollection>() {
            @Override
            protected ProjectCollection doInBackground(Void... params)
            {
                ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
                return projectMapper.getProjects();
            }

            @Override
            protected void onPostExecute(ProjectCollection projects)
            {
                if (isCancelled() || null == projects) {
                    return;
                }

                if (isViewAttached()) {
                    getView().setData(projects);
                }
            }
        };
        mProjectLoader.execute();
    }
}
