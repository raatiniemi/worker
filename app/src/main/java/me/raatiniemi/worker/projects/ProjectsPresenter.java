package me.raatiniemi.worker.projects;

import android.content.Context;

import me.raatiniemi.worker.base.presenter.BasePresenter;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.model.project.ProjectCollection;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends BasePresenter<ProjectsFragment> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "ProjectsPresenter";

    /**
     * Subscription for the project retrieval observable.
     */
    private Subscription mSubscription;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public ProjectsPresenter(Context context) {
        super(context);
    }

    @Override
    public void detachView() {
        super.detachView();

        unsubscribe();
    }

    /**
     * Retrieve the projects.
     *
     * @param projectMapper Mapper to use for the data loading.
     */
    public void getProjects(final ProjectMapper projectMapper) {
        unsubscribe();

        mSubscription = Observable.defer(new Func0<Observable<ProjectCollection>>() {
            @Override
            public Observable<ProjectCollection> call() {
                return Observable.just(projectMapper.getProjects());
            }
        })
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
}
