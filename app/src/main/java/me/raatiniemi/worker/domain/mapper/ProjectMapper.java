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

package me.raatiniemi.worker.domain.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.data.WorkerContract.ProjectColumns;

public class ProjectMapper {
    /**
     * Private constructor, instantiation is not allowed.
     */
    private ProjectMapper() {
    }

    /**
     * Map project from cursor.
     *
     * @param cursor Cursor with data to map to Project.
     * @return Project with data from cursor.
     */
    public static Project map(Cursor cursor) {
        // Map the id and name for the project.
        long id = cursor.getLong(cursor.getColumnIndex(ProjectColumns._ID));
        String name = cursor.getString(cursor.getColumnIndex(ProjectColumns.NAME));
        Project project = new Project(id, name);

        // Map the project description.
        String desc = cursor.getString(cursor.getColumnIndex(ProjectColumns.DESCRIPTION));
        project.setDescription(desc);

        // Map the project archive flag.
        long archived = cursor.getLong(cursor.getColumnIndex(ProjectColumns.ARCHIVED));
        project.setArchived(0 != archived);

        return project;
    }

    /**
     * Map Project to ContentValues.
     *
     * @param project Project to map to ContentValues.
     * @return Mapped ContentValues.
     */
    public static ContentValues map(Project project) {
        ContentValues values = new ContentValues();
        values.put(ProjectColumns.NAME, project.getName());
        values.put(ProjectColumns.DESCRIPTION, project.getDescription());
        values.put(ProjectColumns.ARCHIVED, project.isArchived() ? 1L : 0L);

        return values;
    }
}
