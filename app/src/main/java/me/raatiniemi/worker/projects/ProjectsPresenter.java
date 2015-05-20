package me.raatiniemi.worker.projects;

import android.os.AsyncTask;

import me.raatiniemi.worker.base.presenter.BasePresenter;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.util.ProjectCollection;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends BasePresenter<ProjectsFragment> {
    private AsyncTask<ProjectMapper, Void, ProjectCollection> mProjectLoader;

    @Override
    public void detachView() {
        super.detachView();

        // Before we can detach the view we have to clean up.
        if (null != mProjectLoader && !mProjectLoader.isCancelled()) {
            mProjectLoader.cancel(true);
        }
    }

    /**
     * Load the projects from the mapper and populate the view.
     *
     * @param mapper Mapper to use for the data loading.
     */
    public void loadProjects(ProjectMapper mapper) {
        // If the project loader is already active we have to cancel
        // its running process before we can start a new process.
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
                // If the process have been cancelled or projects have not
                // been initialized (no mapper available), we have to abort.
                if (isCancelled() || null == projects) {
                    return;
                }

                // If the view is attached, populate it with the retrieved projects.
                if (isViewAttached()) {
                    getView().setData(projects);
                }
            }
        };
        mProjectLoader.execute(mapper);
    }
}
