package me.raatiniemi.worker.projects.task;

import android.os.AsyncTask;

import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.projects.ProjectsPresenter;
import me.raatiniemi.worker.util.AsyncTaskResult;
import me.raatiniemi.worker.util.ProjectCollection;

public class ProjectsReadTask
    extends AsyncTask<ProjectMapper, Void, AsyncTaskResult<ProjectCollection>> {
    private ProjectsPresenter mPresenter;

    public ProjectsReadTask(ProjectsPresenter presenter) {
        mPresenter = presenter;
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

        // If the view is attached, populate it with the retrieved data.
        if (mPresenter.isViewAttached()) {
            mPresenter.getView().setData(result.getData());
        }
    }
}
