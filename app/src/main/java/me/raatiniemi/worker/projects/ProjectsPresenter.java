package me.raatiniemi.worker.projects;

import android.content.Context;

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
     * Retrieve the projects.
     */
    public void getProjects() {
        unsubscribe();

        mSubscription = mProvider.getProjects()
            .compose(this.<ProjectCollection>applySchedulers())
            .subscribe(new Action1<ProjectCollection>() {
                @Override
                public void call(ProjectCollection projects) {
                    if (!isViewAttached()) {
                        return;
                    }

                    getView().setData(projects);
                }
            });
    }

    public void createNewProject(Project project) {
        mProvider.createProject(project)
            .compose(this.<Project>applySchedulers())
            .subscribe(new Action1<Project>() {
                @Override
                public void call(Project project) {
                    if (!isViewAttached()) {
                        return;
                    }

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

    public void clockActivityChange(Project project, Date date) {
        mProvider.clockActivityChange(project, date)
            .compose(this.<Project>applySchedulers())
            .subscribe(new Action1<Project>() {
                @Override
                public void call(Project project) {
                    if (!isViewAttached()) {
                        return;
                    }

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
