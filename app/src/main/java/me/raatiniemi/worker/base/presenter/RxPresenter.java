package me.raatiniemi.worker.base.presenter;

import android.content.Context;

import me.raatiniemi.worker.base.view.MvpView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Presenter with functionality for working with RxJava and observables.
 *
 * @param <V> Reference type for the view attached to the presenter.
 */
abstract public class RxPresenter<V extends MvpView> extends BasePresenter<V> {
    /**
     * Subscription for retrieving data for the presenter/view.
     */
    protected Subscription mSubscription;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public RxPresenter(Context context) {
        super(context);
    }

    @Override
    public void detachView() {
        super.detachView();

        // Before we can detach the view, we have to make sure that the
        // subscription have been unsubscribed.
        unsubscribe();
    }

    /**
     * Unsubscribe to the data retrieving subscription.
     */
    protected void unsubscribe() {
        // If have to verify that there is an active subscription.
        if (null != mSubscription && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mSubscription = null;
    }

    /**
     * Apply the default schedulers for the observable.
     *
     * @param <T> Type used with the observable.
     * @return Observable with applied schedulers.
     */
    protected <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
