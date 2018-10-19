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
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.data.repository.exception.ContentResolverApplyBatchException;
import me.raatiniemi.worker.data.repository.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.repository.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.util.Optional;

import static java.util.Objects.requireNonNull;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public class ProjectResolverRepository extends ContentResolverRepository implements ProjectRepository {
    private final ProjectCursorMapper cursorMapper = new ProjectCursorMapper();
    private final ProjectContentValuesMapper contentValuesMapper = new ProjectContentValuesMapper();

    public ProjectResolverRepository(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    private List<Project> fetch(@Nullable Cursor cursor) throws InvalidProjectNameException {
        final List<Project> projects = new ArrayList<>();
        if (isNull(cursor)) {
            return projects;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    projects.add(cursorMapper.transform(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return projects;
    }

    @NonNull
    private Optional<Project> fetchRow(@Nullable Cursor cursor) throws InvalidProjectNameException {
        if (isNull(cursor)) {
            return Optional.empty();
        }

        try {
            if (cursor.moveToFirst()) {
                Project project = cursorMapper.transform(cursor);

                return Optional.of(project);
            }

            return Optional.empty();
        } finally {
            cursor.close();
        }
    }

    @Override
    public Optional<Project> findByName(String projectName) throws InvalidProjectNameException {
        requireNonNull(projectName);

        final Cursor cursor = getContentResolver().query(
                ProviderContract.getProjectStreamUri(),
                ProviderContract.getProjectColumns(),
                ProviderContract.COLUMN_PROJECT_NAME + "=? COLLATE NOCASE",
                new String[]{projectName},
                null
        );
        return fetchRow(cursor);
    }

    @Override
    public List<Project> findAll() throws InvalidProjectNameException {
        final Cursor cursor = getContentResolver().query(
                ProviderContract.getProjectStreamUri(),
                ProviderContract.getProjectColumns(),
                null,
                null,
                ProviderContract.ORDER_BY_PROJECT
        );

        return fetch(cursor);
    }

    @Override
    public Optional<Project> findById(final long id) throws InvalidProjectNameException {
        final Cursor cursor = getContentResolver().query(
                ProviderContract.getProjectItemUri(id),
                ProviderContract.getProjectColumns(),
                null,
                null,
                null
        );
        return fetchRow(cursor);
    }

    @Override
    public Optional<Project> add(final Project project) throws InvalidProjectNameException {
        requireNonNull(project);

        final Uri uri = getContentResolver().insert(
                ProviderContract.getProjectStreamUri(),
                contentValuesMapper.transform(project)
        );
        return findById(Long.parseLong(ProviderContract.getProjectItemId(uri)));
    }

    @Override
    public void remove(final long id) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        // Add operation for removing the registered time for the
        // project. The operation have to be performed before the
        // actual project deletion.
        Uri uri = ProviderContract.getProjectItemTimeUri(id);
        batch.add(ContentProviderOperation.newDelete(uri).build());

        // Add operation for removing the project.
        uri = ProviderContract.getProjectItemUri(id);
        batch.add(ContentProviderOperation.newDelete(uri).build());

        try {
            // Attempt to remove the registered time and project
            // within a single transactional operation.
            getContentResolver().applyBatch(ProviderContract.AUTHORITY, batch);
        } catch (RemoteException | OperationApplicationException e) {
            throw new ContentResolverApplyBatchException(e);
        }
    }
}
