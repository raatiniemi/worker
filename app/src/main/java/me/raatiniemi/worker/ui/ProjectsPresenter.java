package me.raatiniemi.worker.ui;

import android.os.AsyncTask;

import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mvp.BasePresenter;
import me.raatiniemi.worker.util.ProjectCollection;

public class ProjectsPresenter extends BasePresenter<ProjectListFragment>
{
    private AsyncTask<Void, Void, ProjectCollection> mProjectLoader;

    @Override
    public void detachView()
    {
        super.detachView();

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
