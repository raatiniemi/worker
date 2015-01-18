package me.raatiniemi.worker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.raatiniemi.worker.data.Project;

public class ProjectDataSource extends BaseDataSource
{
    private static final String TABLE_NAME = "project";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";

    public static final String CREATE_TABLE_PROJECT =
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
}
