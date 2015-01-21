package me.raatiniemi.worker.database;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import me.raatiniemi.worker.data.Project;

public class ProjectDataSource extends BaseDataSource
{
    private static final String TABLE_NAME = "project";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";

    public static final String CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT NULL " +
        ");";

    public ProjectDataSource(Context context) {
        super(Helper.getInstance(context));
    }

    public ArrayList<Project> getProjects()
    {
        ArrayList<Project> projects = new ArrayList<>();
        String[] columns = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION};

        Cursor cursor = mDatabase.query(TABLE_NAME, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

                Project project = new Project(id, name);
                project.setDescription(description);

                // TODO: Retrieve the registered time for the specified interval (day, week, month).

                projects.add(project);
            } while (cursor.moveToNext());
        }

        return projects;
    }

    public Project findProjectById(long id)
    {
        // TODO: Implement 'findProjectByName'.
        return null;
    }

    public Project findProjectByName(String projectName)
    {
        String[] columns = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION};

        String selection = COLUMN_NAME + "=?";
        String[] selectionArgs = new String[]{projectName};

        Cursor row = mDatabase.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (!row.moveToFirst()) {
            return null;
        }

        long id = row.getLong(row.getColumnIndex(COLUMN_ID));
        String name = row.getString(row.getColumnIndex(COLUMN_NAME));
        String description = row.getString(row.getColumnIndex(COLUMN_DESCRIPTION));

        Project project = new Project(id, name);
        project.setDescription(description);

        return project;
    }

    public Project createNewProject(String projectName)
    {
        // TODO: Implement 'createNewProject'.
        return null;
    }
}
