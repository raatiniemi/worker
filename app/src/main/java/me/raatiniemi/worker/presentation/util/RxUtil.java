package me.raatiniemi.worker.presentation.util;

import android.support.annotation.Nullable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static me.raatiniemi.util.NullUtil.nonNull;

final public class RxUtil {
    private RxUtil() {
    }

    public static void unsubscribeIfNotNull(@Nullable Subscription subscription) {
        if (nonNull(subscription) && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
