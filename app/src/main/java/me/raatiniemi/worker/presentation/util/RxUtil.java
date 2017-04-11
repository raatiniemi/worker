/*
 * Copyright (C) 2017 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.presentation.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;

public final class RxUtil {
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

    @NonNull
    public static <T> Observable.Transformer<T, T> redirectErrors(
            @Nullable PublishSubject<Throwable> subject
    ) {
        return source -> source
                .doOnError(e -> {
                    if (isNull(subject)) {
                        return;
                    }

                    subject.onNext(e);
                })
                .onErrorResumeNext(Observable.empty());
    }

    @NonNull
    public static <T> Observable.Transformer<T, T> hideErrors() {
        return redirectErrors(null);
    }
}
