package me.raatiniemi.worker.projects;

import android.content.Context;
import android.util.Log;

import java.util.Date;

import me.raatiniemi.worker.base.presenter.RxPresenter;
import me.raatiniemi.worker.model.project.Project;
import me.raatiniemi.worker.model.project.ProjectCollection;
import me.raatiniemi.worker.model.project.ProjectProvider;
import rx.functions.Action1;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends RxPresenter<ProjectsFragment> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "ProjectsPresenter";

    /**
     * Provider for working with projects.
     */
    private ProjectProvider mProvider;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public ProjectsPresenter(Context context, ProjectProvider provider) {
        super(context);

        mProvider = provider;
    }

    /**
     * Retrieve the projects and push them to the view.
     */
    public void getProjects() {
        // Before we setup the project subscription we have to cancel
        // the previous one, if available.
        unsubscribe();

        // Setup the subscription for retrieving projects.
        mSubscription = mProvider.getProjects()
            .compose(this.<ProjectCollection>applySchedulers())
            .subscribe(new Action1<ProjectCollection>() {
                @Override
                public void call(ProjectCollection projects) {
                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing projects");
                        return;
                    }

                    // Push the data to the view.
                    getView().setData(projects);
                }
            });
    }

    /**
     * Create a new project and update the view.
     *
     * @param project Project to create.
     */
    public void createNewProject(Project project) {
        mProvider.createProject(project)
            .compose(this.<Project>applySchedulers())
            .subscribe(new Action1<Project>() {
                @Override
                public void call(Project project) {
                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing new project");
                        return;
                    }

                    // Add the new project to the view.
                    getView().addProject(project);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    // TODO: Better and more extendable error handling.
                    getView().showCreateProjectError();
                }
            });
    }

    /**
     * Change the clock activity status for project, i.e. clock in/out.
     *
     * @param project Project to change clock activity status.
     * @param date Date and time to use for the clock activity change.
     */
    public void clockActivityChange(Project project, Date date) {
        mProvider.clockActivityChange(project, date)
            .compose(this.<Project>applySchedulers())
            .subscribe(new Action1<Project>() {
                @Override
                public void call(Project project) {
                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip updating project");
                        return;
                    }

                    // Update the project.
                    getView().updateProject(project);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    // TODO: Better and more extendable error handling.
                    getView().showClockActivityError();
                }
            });
    }
}
