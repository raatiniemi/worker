package me.raatiniemi.worker.projects;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.raatiniemi.worker.base.presenter.RxPresenter;
import me.raatiniemi.worker.model.project.Project;
import me.raatiniemi.worker.model.project.ProjectProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
     * Interval iterator for refreshing active projects.
     */
    private Subscription mRefreshProjects;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     * @param provider Provider for working with projects.
     */
    public ProjectsPresenter(Context context, ProjectProvider provider) {
        super(context);

        mProvider = provider;

        Log.d(TAG, "Subscribe to refresh for active projects");
        mRefreshProjects = refreshActiveProjects();
    }

    @Override
    public void detachView() {
        super.detachView();

        // Before detaching we have to unsubscribe to the refresh of active
        // projects, if it is available and active.
        if (null != mRefreshProjects && !mRefreshProjects.isUnsubscribed()) {
            Log.d(TAG, "Unsubscribe to refresh active projects");
            mRefreshProjects.unsubscribe();
        }
        mRefreshProjects = null;
    }

    private Subscription refreshActiveProjects() {
        // Before we create a new subscription for refreshing active projects
        // we have to unsubscribe to the existing one, if one is available.
        if (null != mRefreshProjects && !mRefreshProjects.isUnsubscribed()) {
            Log.d(TAG, "Unsubscribe to refresh active projects, setup new subscription");
            mRefreshProjects.unsubscribe();
        }
        mRefreshProjects = null;

        return Observable.interval(60, TimeUnit.SECONDS)
            .flatMap(new Func1<Long, Observable<List<Integer>>>() {
                @Override
                public Observable<List<Integer>> call(Long aLong) {
                    List<Integer> positions = new ArrayList<>();

                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip checking active projects");
                        return Observable.just(positions);
                    }

                    // Iterate the projects and collect the index of active projects.
                    List<Project> data = getView().getData();
                    for (Project project : data) {
                        if (project.isActive()) {
                            Log.d(TAG, "Queuing refresh of project: " + project.getName());
                            positions.add(data.indexOf(project));
                        }
                    }
                    return Observable.just(positions);
                }
            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Integer>>() {
                @Override
                public void call(List<Integer> positions) {
                    // Check that we have found active projects to refresh.
                    if (positions.isEmpty()) {
                        Log.d(TAG, "No projects are active, nothing to refresh");
                        return;
                    }

                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip refreshing active projects");
                        return;
                    }

                    // Refresh the active projects that have been found.
                    Log.d(TAG, "Refreshing active projects");
                    getView().refreshPositions(positions);
                }
            });
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
            .map(new Func1<List<Project>, List<Project>>() {
                @Override
                public List<Project> call(List<Project> projects) {
                    // Populate the projects with the registered time.
                    for (Project project : projects) {
                        int index = projects.indexOf(project);

                        project = mProvider.getTime(project);
                        projects.set(index, project);
                    }

                    return projects;
                }
            })
            .compose(this.<List<Project>>applySchedulers())
            .subscribe(new Action1<List<Project>>() {
                @Override
                public void call(List<Project> projects) {
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
