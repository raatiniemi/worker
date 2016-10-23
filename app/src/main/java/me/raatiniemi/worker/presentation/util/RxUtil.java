package me.raatiniemi.worker.presentation.util;

import android.support.annotation.Nullable;

import rx.Subscription;

import static me.raatiniemi.util.NullUtil.nonNull;

public class RxUtil {
    public static void unsubscribeIfNotNull(@Nullable Subscription subscription) {
        if (nonNull(subscription) && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
