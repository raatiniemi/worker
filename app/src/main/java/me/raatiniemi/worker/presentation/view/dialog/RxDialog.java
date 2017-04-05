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

package me.raatiniemi.worker.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Objects;

import rx.Observable;
import rx.subscriptions.Subscriptions;

public final class RxDialog {
    private static final Integer NEGATIVE = 0;
    private static final Integer POSITIVE = 1;

    private RxDialog() {
    }

    @NonNull
    public static Observable<Integer> build(@NonNull Context context, int title, int message) {
        return Observable.create(subscriber -> {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        subscriber.onNext(POSITIVE);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        subscriber.onNext(NEGATIVE);
                        subscriber.onCompleted();
                    })
                    .create();

            alertDialog.show();
            subscriber.add(Subscriptions.create(alertDialog::dismiss));
        });
    }

    public static boolean isPositive(@NonNull Integer which) {
        return Objects.equals(POSITIVE, which);
    }
}
