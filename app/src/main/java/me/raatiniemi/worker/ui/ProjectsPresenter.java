package me.raatiniemi.worker.ui;

import android.os.AsyncTask;

import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mvp.BasePresenter;
import me.raatiniemi.worker.util.ProjectCollection;

public class ProjectsPresenter extends BasePresenter<ProjectListFragment> {
    private AsyncTask<ProjectMapper, Void, ProjectCollection> mProjectLoader;

    @Override
    public void detachView() {
        super.detachView();

        if (null != mProjectLoader && !mProjectLoader.isCancelled()) {
            mProjectLoader.cancel(true);
        }
    }

    public void loadProjects(ProjectMapper mapper) {
        if (null != mProjectLoader && !mProjectLoader.isCancelled()) {
            mProjectLoader.cancel(true);
        }

        mProjectLoader = new AsyncTask<ProjectMapper, Void, ProjectCollection>() {
            @Override
            protected ProjectCollection doInBackground(ProjectMapper... params) {
                // Check that we have received the project mapper as argument.
                if (0 == params.length || null == params[0]) {
                    return null;
                }

                // Retrieve the projects from the project mapper.
                ProjectMapper mapper = params[0];
                return mapper.getProjects();
            }

            @Override
            protected void onPostExecute(ProjectCollection projects) {
                if (isCancelled() || null == projects) {
                    return;
                }

                if (isViewAttached()) {
                    getView().setData(projects);
                }
            }
        };
        mProjectLoader.execute(mapper);
    }
}
