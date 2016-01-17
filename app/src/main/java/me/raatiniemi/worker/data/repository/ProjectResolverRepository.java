/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.data.repository;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.data.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.data.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import rx.Observable;
import rx.android.content.ContentObservable;
import rx.functions.Func0;
import rx.functions.Func1;

public class ProjectResolverRepository extends ContentResolverRepository<ProjectCursorMapper> implements ProjectRepository {
    /**
     * @inheritDoc
     */
    public ProjectResolverRepository(
            @NonNull ContentResolver contentResolver,
            @NonNull ProjectCursorMapper cursorMapper
    ) {
        super(contentResolver, cursorMapper);
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
                return getCursorMapper().transform(cursor);
            }
        });
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Observable<Project> get(final long id) {
        return Observable.just(id)
                .flatMap(new Func1<Long, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(final Long id) {
                        Cursor cursor = getContentResolver().query(
                                ProjectContract.getItemUri(id),
                                ProjectContract.COLUMNS,
                                null,
                                null,
                                null
                        );

                        return ContentObservable.fromCursor(cursor);
                    }
                })
                .map(new Func1<Cursor, Project>() {
                    @Override
                    public Project call(final Cursor cursor) {
                        return getCursorMapper().transform(cursor);
                    }
                })
                .first();
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Observable<Project> add(final String name) {
        final ContentValues values = new ContentValues();
        values.put(ProjectColumns.NAME, name);

        return Observable.just(values)
                .flatMap(new Func1<ContentValues, Observable<String>>() {
                    @Override
                    public Observable<String> call(final ContentValues values) {
                        try {
                            final Uri uri = getContentResolver().insert(
                                    ProjectContract.getStreamUri(),
                                    values
                            );

                            return Observable.just(ProjectContract.getItemId(uri));
                        } catch (Throwable e) {
                            return Observable.error(new ProjectAlreadyExistsException());
                        }
                    }
                })
                .flatMap(new Func1<String, Observable<Project>>() {
                    @Override
                    public Observable<Project> call(final String id) {
                        return get(Long.valueOf(id));
                    }
                });
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Observable<Long> remove(final long id) {
        return Observable.just(id)
                .flatMap(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(final Long id) {
                        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

                        // Add operation for removing the registered time for the
                        // project. The operation have to be performed before the
                        // actual project deletion.
                        Uri uri = ProjectContract.getItemTimeUri(id);
                        batch.add(ContentProviderOperation.newDelete(uri).build());

                        // Add operation for removing the project.
                        uri = ProjectContract.getItemUri(id);
                        batch.add(ContentProviderOperation.newDelete(uri).build());

                        try {
                            // Attempt to remove the registered time and project
                            // within a single transactional operation.
                            getContentResolver().applyBatch(WorkerContract.AUTHORITY, batch);
                            return Observable.just(id);
                        } catch (RemoteException | OperationApplicationException e) {
                            return Observable.error(e);
                        }
                    }
                });
    }
}
