package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;

import me.raatiniemi.worker.domain.DomainObject;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;

public class ProjectMapper extends AbstractMapper
{
    private static final String TABLE_NAME = "project";

    private interface Columns
    {
        String NAME = "name";

        String DESCRIPTION = "description";
    }

    public static final String CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " ( " +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.NAME + " TEXT NOT NULL, " +
            Columns.DESCRIPTION + " TEXT NULL, " +
            "UNIQUE (" + Columns.NAME + ") ON CONFLICT ROLLBACK" +
        ");";

    private TimeMapper mTimeMapper;

    public ProjectMapper(TimeMapper timeMapper)
    {
        super();

        mTimeMapper = timeMapper;
    }

    protected String getTable()
    {
        return TABLE_NAME;
    }

    protected String[] getColumns()
    {
        return new String[]{
            BaseColumns._ID,
            Columns.NAME,
            Columns.DESCRIPTION
        };
    }

    protected DomainObject load(Cursor row)
    {
        long id = row.getLong(row.getColumnIndex(BaseColumns._ID));
        String name = row.getString(row.getColumnIndex(Columns.NAME));
        String description = row.getString(row.getColumnIndex(Columns.DESCRIPTION));

        Project project = new Project(id, name);
        project.setDescription(description);

        if (null != mTimeMapper) {
            ArrayList<Time> time = mTimeMapper.findTimeByProject(project);
            for (Time item: time) {
                project.addTime(item);
            }
        }

        return project;
    }

    public ArrayList<Project> getProjects()
    {
        ArrayList<Project> result = new ArrayList<>();

        Cursor rows = mDatabase.query(getTable(), getColumns(), null, null, null, null, null);
        if (rows.moveToFirst()) {
            do {
                Project project = (Project) load(rows);
                result.add(project);
            } while (rows.moveToNext());
        }

        return result;
    }

    public Project find(String name)
    {
        String selection = Columns.NAME + "=?";
        String[] selectionArgs = new String[]{name};

        Cursor row = mDatabase.query(getTable(), getColumns(), selection, selectionArgs, null, null, null);
        if (!row.moveToFirst()) {
            return null;
        }

        return (Project) load(row);
    }

    public Project insert(Project project) throws ProjectAlreadyExistsException
    {
        if (null != find(project.getName())) {
            throw new ProjectAlreadyExistsException();
        }

        ContentValues values = new ContentValues();
        values.put(Columns.NAME, project.getName());
        values.put(Columns.DESCRIPTION, project.getDescription());

        long id = mDatabase.insert(getTable(), null, values);
        return (Project) find(id);
    }
}
