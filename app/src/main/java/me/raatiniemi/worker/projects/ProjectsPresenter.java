package me.raatiniemi.worker.projects;

import android.os.AsyncTask;

import me.raatiniemi.worker.base.presenter.BasePresenter;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.projects.task.ProjectsReadTask;
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

        mProjectLoader = new ProjectsReadTask(this);
        mProjectLoader.execute(mapper);
    }
}
