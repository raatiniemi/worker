package me.raatiniemi.worker.projects;

import me.raatiniemi.worker.base.presenter.BasePresenter;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.projects.task.ProjectsReadTask;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends BasePresenter<ProjectsFragment> {
    private ProjectsReadTask mProjectReadTask;

    @Override
    public void detachView() {
        super.detachView();

        // Before we can detach the view we have to clean up.
        if (null != mProjectReadTask && !mProjectReadTask.isCancelled()) {
            mProjectReadTask.cancel(true);
        }
        mProjectReadTask = null;
    }

    /**
     * Load the projects from the mapper and populate the view.
     *
     * @param mapper Mapper to use for the data loading.
     */
    public void loadProjects(ProjectMapper mapper) {
        // If the project loader is already active we have to cancel
        // its running process before we can start a new process.
        if (null != mProjectReadTask && !mProjectReadTask.isCancelled()) {
            mProjectReadTask.cancel(true);
        }

        mProjectReadTask = new ProjectsReadTask(this);
        mProjectReadTask.execute(mapper);
    }
}
