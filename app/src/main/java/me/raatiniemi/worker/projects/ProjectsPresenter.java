package me.raatiniemi.worker.projects;

import android.content.Context;

import java.util.Date;

import me.raatiniemi.worker.base.presenter.BasePresenter;
import me.raatiniemi.worker.model.project.Project;
import me.raatiniemi.worker.model.project.ProjectCollection;
import me.raatiniemi.worker.model.project.ProjectProvider;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends BasePresenter<ProjectsFragment> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "ProjectsPresenter";

    private ProjectProvider mProvider;

    /**
     * Subscription for the project retrieval observable.
     */
    private Subscription mSubscription;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public ProjectsPresenter(Context context, ProjectProvider provider) {
        super(context);

        mProvider = provider;
    }

    @Override
    public void detachView() {
        super.detachView();

        unsubscribe();
    }

    /**
     * Retrieve the projects.
     */
    public void getProjects() {
        unsubscribe();

        mSubscription = mProvider.getProjects()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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

    protected void unsubscribe() {
        if (null != mSubscription && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mSubscription = null;
    }

    public void createNewProject(Project project) {
        mProvider.createProject(project)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
                    getView().showCreateProjectError();
                }
            });
    }

    public void clockActivityChange(Project project, Date date) {
        mProvider.clockActivityChange(project, date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
                    getView().showClockActivityError();
                }
            });
    }
}
