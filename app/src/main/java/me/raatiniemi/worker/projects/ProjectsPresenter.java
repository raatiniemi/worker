package me.raatiniemi.worker.projects;

import android.os.AsyncTask;

import me.raatiniemi.worker.base.presenter.BasePresenter;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.util.AsyncTaskResult;
import me.raatiniemi.worker.util.ProjectCollection;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends BasePresenter<ProjectsFragment> {
    private AsyncTask<ProjectMapper, Void, AsyncTaskResult<ProjectCollection>> mProjectLoader;

    @Override
    public void detachView() {
        super.detachView();

        // Before we can detach the view we have to clean up.
        if (null != mProjectLoader && !mProjectLoader.isCancelled()) {
            mProjectLoader.cancel(true);
        }
        mProjectLoader = null;
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

        mProjectLoader = new AsyncTask<ProjectMapper, Void, AsyncTaskResult<ProjectCollection>>() {
            @Override
            protected AsyncTaskResult<ProjectCollection> doInBackground(ProjectMapper... params) {
                // Check that we have received the project mapper as argument.
                if (0 == params.length || null == params[0]) {
                    return null;
                }

                AsyncTaskResult<ProjectCollection> result;
                try {
                    // Retrieve the projects from the project mapper.
                    ProjectMapper mapper = params[0];
                    result = new AsyncTaskResult<>(mapper.getProjects());
                } catch (Throwable e) {
                    result = new AsyncTaskResult<>(e);
                }
                return result;
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<ProjectCollection> result) {
                // If the process have been cancelled or result have not
                // been initialized (no mapper available), we have to abort.
                if (isCancelled() || null == result) {
                    return;
                }

                // If the view is attached, populate it with the retrieved data.
                if (isViewAttached()) {
                    getView().setData(result.getData());
                }
            }
        };
        mProjectLoader.execute(mapper);
    }
}
