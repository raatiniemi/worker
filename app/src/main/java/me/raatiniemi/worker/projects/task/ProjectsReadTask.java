package me.raatiniemi.worker.projects.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.projects.ProjectsPresenter;
import me.raatiniemi.worker.util.AsyncTaskResult;
import me.raatiniemi.worker.util.ProjectCollection;

public class ProjectsReadTask
    extends AsyncTask<ProjectMapper, Void, AsyncTaskResult<ProjectCollection>> {
    /**
     * Store a weak reference to the presenter.
     */
    private WeakReference<ProjectsPresenter> mPresenterRef;

    public ProjectsReadTask(ProjectsPresenter presenter) {
        mPresenterRef = new WeakReference<>(presenter);
    }

    /**
     * Check if the presenter is available.
     *
     * @return True if the presenter is available, otherwise false.
     */
    private boolean hasPresenter() {
        return null != mPresenterRef && null != mPresenterRef.get();
    }

    /**
     * Retrieve the presenter.
     *
     * @return Presenter using the task.
     */
    private ProjectsPresenter getPresenter() {
        return mPresenterRef.get();
    }

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

        // If we don't have a reference for the presenter, or if the
        // presenters view is not attached we can't proceed.
        if (!hasPresenter() || !getPresenter().isViewAttached()) {
            return;
        }

        // Populate the presenter view with the retrieved data.
        getPresenter().getView()
            .setData(result.getData());
    }
}
