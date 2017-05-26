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

package me.raatiniemi.worker.data.repository.mapper;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.provider.ProviderContract.ProjectColumns;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;

/**
 * Handle transformation from {@link Cursor} to {@link Project}.
 */
public class ProjectCursorMapper implements CursorMapper<Project> {
    @Override
    @NonNull
    public Project transform(@NonNull Cursor cursor) throws InvalidProjectNameException {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProjectColumns.NAME));

        return Project.builder(name)
                .id(id)
                .build();
    }
}
