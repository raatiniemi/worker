package me.raatiniemi.worker.projects;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.raatiniemi.worker.base.presenter.RxPresenter;
import me.raatiniemi.worker.model.domain.project.Project;
import me.raatiniemi.worker.model.domain.project.ProjectProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
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
     * @param context  Context used with the presenter.
     * @param provider Provider for working with projects.
     */
    public ProjectsPresenter(Context context, ProjectProvider provider) {
        super(context);

        mProvider = provider;
    }

    /**
     * Get the positions for the active projects from the view.
     *
     * @return Positions for the active projects.
     */
    private List<Integer> getPositionsForActiveProjects() {
        List<Integer> positions = new ArrayList<>();

        // Check that we still have the view attached.
        if (!isViewAttached()) {
            Log.d(TAG, "View is not attached, skip checking active projects");
            return positions;
        }

        // Iterate the projects and collect the index of active projects.
        List<Project> data = getView().getData();
        for (Project project : data) {
            if (project.isActive()) {
                Log.d(TAG, "Queuing refresh of project: " + project.getName());
                positions.add(data.indexOf(project));
            }
        }
        return positions;
    }

    /**
     * Refresh the project positions on the view.
     *
     * @param positions Positions on the view to refresh.
     */
    private void refreshActiveProjects(List<Integer> positions) {
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

    /**
     * Setup the subscription for refreshing active projects.
     */
    public void beginRefreshingActiveProjects() {
        // Before we create a new subscription for refreshing active projects
        // we have to unsubscribe to the existing one, if one is available.
        stopRefreshingActiveProjects();

        Log.d(TAG, "Subscribe to the refresh of active projects");
        mRefreshProjects = Observable.interval(60, TimeUnit.SECONDS)
                .map(new Func1<Long, List<Integer>>() {
                    @Override
                    public List<Integer> call(Long aLong) {
                        return getPositionsForActiveProjects();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onNext(List<Integer> positions) {
                        Log.d(TAG, "beginRefreshingActiveProjects onNext");

                        // Push the data to the view.
                        refreshActiveProjects(positions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "beginRefreshingActiveProjects onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get positions: " + e.getMessage());

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        // Push the error to the view.
                        getView().showError(e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "beginRefreshingActiveProjects onCompleted");
                    }
                });
    }

    /**
     * Unsubscribe to the refresh of active projects.
     */
    public void stopRefreshingActiveProjects() {
        if (null != mRefreshProjects && !mRefreshProjects.isUnsubscribed()) {
            Log.d(TAG, "Unsubscribe to the refresh of active projects");
            mRefreshProjects.unsubscribe();
        }
        mRefreshProjects = null;
    }

    /**
     * Refresh active projects.
     */
    public void refreshActiveProjects() {
        Log.d(TAG, "Refreshing active projects");
        Observable.defer(new Func0<Observable<List<Integer>>>() {
            @Override
            public Observable<List<Integer>> call() {
                return Observable.just(getPositionsForActiveProjects());
            }
        }).compose(this.<List<Integer>>applySchedulers())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onNext(List<Integer> positions) {
                        Log.d(TAG, "refreshActiveProjects onNext");

                        // Push the data to the view.
                        refreshActiveProjects(positions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "refreshActiveProjects onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get positions: " + e.getMessage());

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        // Push the error to the view.
                        getView().showError(e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "refreshActiveProjects onCompleted");
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
                .subscribe(new Subscriber<List<Project>>() {
                    @Override
                    public void onNext(List<Project> projects) {
                        Log.d(TAG, "getProjects onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing projects");
                            return;
                        }

                        // Push the data to the view.
                        getView().setData(projects);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "getProjects onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get projects: " + e.getMessage());

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        // Push the error to the view.
                        getView().showError(e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "getProjects onCompleted");
                    }
                });
    }

    /**
     * Create a new project and update the view.
     *
     * @param project Project to create.
     * @return Observable emitting the created project.
     */
    public Observable<Project> createNewProject(Project project) {
        return mProvider.createProject(project)
                .compose(this.<Project>applySchedulers())
                .doOnNext(new Action1<Project>() {
                    @Override
                    public void call(Project project) {
                        Log.d(TAG, "createNewProject onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing new project");
                            return;
                        }

                        // Add the new project to the view.
                        getView().addProject(project);
                    }
                });
    }

    /**
     * Delete project with registered time.
     *
     * @param project Project to be deleted.
     */
    public void deleteProject(Project project) {
        mProvider.deleteProject(project)
                .compose(this.<Project>applySchedulers())
                .subscribe(new Subscriber<Project>() {
                    @Override
                    public void onNext(Project project) {
                        Log.d(TAG, "deleteProject onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing project deletion");
                            return;
                        }

                        // Attempt to delete the project from the view.
                        getView().deleteProject(project);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "deleteProject onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to delete project: " + e.getMessage());

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        // Push the error to the view.
                        getView().showError(e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "deleteProject onCompleted");
                    }
                });
    }

    /**
     * Change the clock activity status for project, i.e. clock in/out.
     *
     * @param project Project to change clock activity status.
     * @param date    Date and time to use for the clock activity change.
     */
    public void clockActivityChange(Project project, Date date) {
        mProvider.clockActivityChange(project, date)
                .compose(this.<Project>applySchedulers())
                .subscribe(new Subscriber<Project>() {
                    @Override
                    public void onNext(Project project) {
                        Log.d(TAG, "clockActivityChange onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip updating project");
                            return;
                        }

                        // Update the project.
                        getView().updateProject(project);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "clockActivityChange onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to change clock activity: " + e.getMessage());

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        // Push the error to the view.
                        getView().showError(e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "clockActivityChange onCompleted");
                    }
                });
    }
}
