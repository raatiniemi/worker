package me.raatiniemi.worker.model.domain.project;

import android.content.ContentValues;
import android.database.Cursor;

import me.raatiniemi.worker.provider.WorkerContract.ProjectColumns;

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
        project.setArchived(archived);

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
        values.put(ProjectColumns.ARCHIVED, project.getArchived());

        return values;
    }
}
