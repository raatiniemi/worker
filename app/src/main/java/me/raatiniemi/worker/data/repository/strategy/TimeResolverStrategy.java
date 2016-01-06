/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.data.repository.strategy;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.WorkerContract.TimeContract;
import me.raatiniemi.worker.data.mapper.TimeEntityMapper;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.domain.mapper.TimeMapper;
import rx.Observable;
import rx.android.content.ContentObservable;
import rx.functions.Func1;

public class TimeResolverStrategy extends ContentResolverStrategy<TimeEntityMapper> implements TimeStrategy {
    /**
     * @inheritDoc
     */
    public TimeResolverStrategy(
            @NonNull ContentResolver contentResolver,
            @NonNull TimeEntityMapper entityMapper
    ) {
        super(contentResolver, entityMapper);
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Observable<Time> get(final long id) {
        return Observable.just(id)
                .flatMap(new Func1<Long, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(Long id) {
                        Cursor cursor = getContentResolver().query(
                                TimeContract.getItemUri(id),
                                TimeContract.COLUMNS,
                                null,
                                null,
                                null
                        );

                        return ContentObservable.fromCursor(cursor);
                    }
                })
                .map(new Func1<Cursor, Time>() {
                    @Override
                    public Time call(Cursor cursor) {
                        // TODO: Map to TimeEntity when Time have been refactored.
                        return TimeMapper.map(cursor);
                    }
                })
                .first();
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Observable<Long> remove(final long id) {
        return Observable.just(id)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(final Long id) {
                        getContentResolver().delete(
                                TimeContract.getItemUri(id),
                                null,
                                null
                        );

                        return id;
                    }
                });
    }
}
