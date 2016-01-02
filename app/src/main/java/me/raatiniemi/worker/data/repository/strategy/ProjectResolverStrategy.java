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

import me.raatiniemi.worker.data.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.mapper.ProjectEntityMapper;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.mapper.ProjectMapper;
import rx.Observable;
import rx.android.content.ContentObservable;
import rx.functions.Func0;
import rx.functions.Func1;

public class ProjectResolverStrategy extends ContentResolverStrategy<ProjectEntityMapper> implements ProjectStrategy {
    /**
     * @inheritDoc
     */
    public ProjectResolverStrategy(
            @NonNull ContentResolver contentResolver,
            @NonNull ProjectEntityMapper entityMapper
    ) {
        super(contentResolver, entityMapper);
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Observable<Project> get() {
        return Observable.defer(new Func0<Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call() {
                Cursor cursor = getContentResolver().query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                );

                return ContentObservable.fromCursor(cursor);
            }
        }).map(new Func1<Cursor, Project>() {
            @Override
            public Project call(Cursor cursor) {
                // TODO: Map to ProjectEntity when Project have been refactored.
                return ProjectMapper.map(cursor);
            }
        });
    }
}
