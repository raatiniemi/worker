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
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.data.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.repository.query.ContentResolverQuery;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.query.Criteria;

public class ProjectResolverRepository
        extends ContentResolverRepository<ProjectCursorMapper, ProjectContentValuesMapper>
        implements ProjectRepository {
    /**
     * @inheritDoc
     */
    public ProjectResolverRepository(
            @NonNull ContentResolver contentResolver,
            @NonNull ProjectCursorMapper cursorMapper,
            @NonNull final ProjectContentValuesMapper contentValuesMapper
    ) {
        super(contentResolver, cursorMapper, contentValuesMapper);
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Project> matching(final Criteria criteria) throws InvalidProjectNameException {
        final List<Project> projects = new ArrayList<>();

        ContentResolverQuery query = ContentResolverQuery.from(criteria);
        final Cursor cursor = getContentResolver().query(
                ProjectContract.getStreamUri(),
                ProjectContract.COLUMNS,
                query.getSelection(),
                query.getSelectionArgs(),
                null
        );
        if (null == cursor) {
            return projects;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    projects.add(getCursorMapper().transform(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return projects;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Project> get() throws InvalidProjectNameException {
        final List<Project> projects = new ArrayList<>();

        final Cursor cursor = getContentResolver().query(
                ProjectContract.getStreamUri(),
                ProjectContract.COLUMNS,
                null,
                null,
                null
        );
        if (null == cursor) {
            return projects;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    projects.add(getCursorMapper().transform(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return projects;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Project get(final long id) throws InvalidProjectNameException {
        final Cursor cursor = getContentResolver().query(
                ProjectContract.getItemUri(id),
                ProjectContract.COLUMNS,
                null,
                null,
                null
        );
        if (null == cursor) {
            return null;
        }

        Project project = null;
        try {
            if (cursor.moveToFirst()) {
                project = getCursorMapper().transform(cursor);
            }
        } finally {
            cursor.close();
        }

        return project;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Project add(final Project project) throws InvalidProjectNameException {
        final Uri uri = getContentResolver().insert(
                ProjectContract.getStreamUri(),
                getContentValuesMapper().transform(project)
        );
        return get(Long.valueOf(ProjectContract.getItemId(uri)));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void remove(final long id) {
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
        } catch (RemoteException | OperationApplicationException e) {
            // TODO: Refactor to allow for `remove` to throw DomainException.
            throw new RuntimeException(e);
        }
    }
}
