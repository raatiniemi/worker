package me.raatiniemi.worker.base.presenter;

import android.content.Context;

import me.raatiniemi.worker.base.view.MvpView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Presenter with functionality for working with RxJava and observables.
 *
 * @param <V> Reference type for the view attached to the presenter.
 */
abstract public class RxPresenter<V extends MvpView> extends BasePresenter<V> {
    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public RxPresenter(Context context) {
        super(context);
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
