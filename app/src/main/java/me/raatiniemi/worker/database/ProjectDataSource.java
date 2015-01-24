package me.raatiniemi.worker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import me.raatiniemi.worker.data.Project;
import me.raatiniemi.worker.data.Time;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;

public class ProjectDataSource
{
    public interface Structure extends BaseColumns
    {
        public static final String TABLE_NAME = "project";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_DESCRIPTION = "description";
    }

    public static final String CREATE_TABLE =
        "CREATE TABLE " + Structure.TABLE_NAME + " ( " +
            Structure.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Structure.COLUMN_NAME + " TEXT NOT NULL, " +
            Structure.COLUMN_DESCRIPTION + " TEXT NULL " +
        ");";

    protected Helper mHelper;

    protected SQLiteDatabase mDatabase;

    public ProjectDataSource(Helper helper)
    {
        mHelper = helper;
        mDatabase = mHelper.getWritableDatabase();
    }

    public ProjectDataSource(Context context)
    {
        this(Helper.getInstance(context));
    }

    public ArrayList<Project> getProjects()
    {
        ArrayList<Project> projects = new ArrayList<>();
        String[] columns = new String[]{
            Structure.COLUMN_ID,
            Structure.COLUMN_NAME,
            Structure.COLUMN_DESCRIPTION
        };

        TimeDataSource timeDataSource = new TimeDataSource(mHelper);

        Cursor cursor = mDatabase.query(Structure.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(Structure.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(Structure.COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(Structure.COLUMN_DESCRIPTION));

                Project project = new Project(id, name);
                project.setDescription(description);

                // TODO: Retrieve the registered time for the specified interval (day, week, month).
                ArrayList<Time> time = timeDataSource.getTimeByProjectId(project.getId());
                project.setTime(time);

                projects.add(project);
            } while (cursor.moveToNext());
        }

        return projects;
    }

    public Project findProjectById(long id)
    {
        String[] columns = new String[]{
            Structure.COLUMN_NAME,
            Structure.COLUMN_DESCRIPTION
        };

        String selection = Structure.COLUMN_ID +"="+ id;

        Cursor row = mDatabase.query(Structure.TABLE_NAME, columns, selection, null, null, null, null);
        if (!row.moveToFirst()) {
            Log.d("findProjectById", "No project exists with id: "+ id);
            return null;
        }

        String name = row.getString(row.getColumnIndex(Structure.COLUMN_NAME));
        String description = row.getString(row.getColumnIndex(Structure.COLUMN_DESCRIPTION));

        Project project = new Project(id, name);
        project.setDescription(description);

        return project;
    }

    public Project findProjectByName(String name)
    {
        String[] columns = new String[]{
            Structure.COLUMN_ID,
            Structure.COLUMN_DESCRIPTION
        };

        String selection = Structure.COLUMN_NAME + "=?";
        String[] selectionArgs = new String[]{name};

        Cursor row = mDatabase.query(Structure.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (!row.moveToFirst()) {
            Log.i("findProjectByName", "No project exists with name: "+ name);
            return null;
        }

        long id = row.getLong(row.getColumnIndex(Structure.COLUMN_ID));
        String description = row.getString(row.getColumnIndex(Structure.COLUMN_DESCRIPTION));

        Project project = new Project(id, name);
        project.setDescription(description);

        return project;
    }

    public Project createNewProject(String name) throws ProjectAlreadyExistsException
    {
        if (null != findProjectByName(name)) {
            Log.i("ProjectDataSource", "Project with name '"+ name +"' already exists");
            throw new ProjectAlreadyExistsException();
        }

        ContentValues values = new ContentValues();
        values.put(Structure.COLUMN_NAME, name);

        // Insert the new project name and retrieve the data.
        long id = mDatabase.insert(Structure.TABLE_NAME, null, values);
        return findProjectById(id);
    }
}
