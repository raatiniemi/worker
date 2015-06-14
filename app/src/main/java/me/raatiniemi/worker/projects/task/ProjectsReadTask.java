package me.raatiniemi.worker.projects.task;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.model.project.ProjectCollection;
import me.raatiniemi.worker.projects.ProjectsPresenter;
import me.raatiniemi.worker.util.AsyncTaskResult;

/**
 * Reads projects via the mapper in a background thread.
 */
public class ProjectsReadTask
    extends AsyncTask<Void, Void, AsyncTaskResult<ProjectCollection>> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "ProjectsReadTask";

    /**
     * Store a weak reference to the presenter.
     */
    private WeakReference<ProjectsPresenter> mPresenterRef;

    /**
     * Mapper used to retrieve the projects.
     */
    private ProjectMapper mProjectMapper;

    /**
     * Construct the task with the presenter and mapper.
     *
     * @param presenter Presenter using the task.
     * @param projectMapper Mapper used to retrieve the projects.
     */
    public ProjectsReadTask(ProjectsPresenter presenter, ProjectMapper projectMapper) {
        mPresenterRef = new WeakReference<>(presenter);
        mProjectMapper = projectMapper;
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
    protected AsyncTaskResult<ProjectCollection> doInBackground(Void... voids) {
        // Check that we have received the project mapper as argument.
        if (null == mProjectMapper) {
            Log.e(TAG, "No ProjectMapper is available");
            return null;
        }

        AsyncTaskResult<ProjectCollection> result;
        try {
            // Retrieve the projects from the project mapper.
            result = new AsyncTaskResult<>(mProjectMapper.getProjects());
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
            Log.i(TAG, "Read task was cancelled or unable to retrieve data");
            return;
        }

        // If we don't have a reference for the presenter, or if the
        // presenters view is not attached we can't proceed.
        if (!hasPresenter() || !getPresenter().isViewAttached()) {
            Log.i(TAG, "Presenter or its view is not available");
            return;
        }

        // Populate the presenter view with the retrieved data.
        getPresenter().getView()
            .setData(result.getData());
    }
}
