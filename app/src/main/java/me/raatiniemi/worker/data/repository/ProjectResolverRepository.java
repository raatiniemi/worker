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
import me.raatiniemi.worker.data.repository.exception.ContentResolverApplyBatchException;
import me.raatiniemi.worker.data.repository.query.ContentResolverQuery;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.query.Criteria;

import static me.raatiniemi.worker.util.NullUtil.isNull;

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
        ContentResolverQuery query = ContentResolverQuery.from(criteria);
        final Cursor cursor = getContentResolver().query(
                ProjectContract.getStreamUri(),
                ProjectContract.getColumns(),
                query.getSelection(),
                query.getSelectionArgs(),
                null
        );

        return fetch(cursor);
    }

    private List<Project> fetch(Cursor cursor) throws InvalidProjectNameException {
        final List<Project> projects = new ArrayList<>();

        if (isNull(cursor)) {
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
        final Cursor cursor = getContentResolver().query(
                ProjectContract.getStreamUri(),
                ProjectContract.getColumns(),
                null,
                null,
                ProjectContract.ORDER_BY
        );

        return fetch(cursor);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Project get(final long id) throws InvalidProjectNameException {
        final Cursor cursor = getContentResolver().query(
                ProjectContract.getItemUri(id),
                ProjectContract.getColumns(),
                null,
                null,
                null
        );
        if (isNull(cursor)) {
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
        return get(Long.parseLong(ProjectContract.getItemId(uri)));
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
            throw new ContentResolverApplyBatchException(e);
        }
    }
}
