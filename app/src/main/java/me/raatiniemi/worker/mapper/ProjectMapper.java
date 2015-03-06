package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.provider.WorkerContract.*;
import me.raatiniemi.worker.provider.WorkerDatabase.*;

public class ProjectMapper extends AbstractMapper<Project>
{
    public static final String CREATE_TABLE =
        "CREATE TABLE " + Tables.PROJECT + " ( " +
            ProjectColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ProjectColumns.NAME + " TEXT NOT NULL, " +
            ProjectColumns.DESCRIPTION + " TEXT NULL, " +
            ProjectColumns.ARCHIVED + " INTEGER DEFAULT 0, " +
            "UNIQUE (" + ProjectColumns.NAME + ") ON CONFLICT ROLLBACK" +
        ");";

    private TimeMapper mTimeMapper;

    public ProjectMapper(TimeMapper timeMapper)
    {
        super();

        mTimeMapper = timeMapper;
    }

    protected String getTable()
    {
        return Tables.PROJECT;
    }

    protected String[] getColumns()
    {
        return new String[]{
            ProjectColumns.ID,
            ProjectColumns.NAME,
            ProjectColumns.DESCRIPTION,
            ProjectColumns.ARCHIVED
        };
    }

    protected Project load(Cursor row)
    {
        long id = row.getLong(row.getColumnIndex(ProjectColumns.ID));
        String name = row.getString(row.getColumnIndex(ProjectColumns.NAME));
        String description = row.getString(row.getColumnIndex(ProjectColumns.DESCRIPTION));
        long archived = row.getLong(row.getColumnIndex(ProjectColumns.ARCHIVED));

        Project project = new Project(id, name);
        project.setDescription(description);
        project.setArchived(archived);

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
                Project project = load(rows);
                result.add(project);
            } while (rows.moveToNext());
        }

        return result;
    }

    public Project find(String name)
    {
        String selection = ProjectColumns.NAME + "=?";
        String[] selectionArgs = new String[]{name};

        Cursor row = mDatabase.query(getTable(), getColumns(), selection, selectionArgs, null, null, null);
        if (!row.moveToFirst()) {
            return null;
        }

        return load(row);
    }

    public Project insert(Project project) throws ProjectAlreadyExistsException
    {
        if (null != find(project.getName())) {
            throw new ProjectAlreadyExistsException();
        }

        ContentValues values = new ContentValues();
        values.put(ProjectColumns.NAME, project.getName());
        values.put(ProjectColumns.DESCRIPTION, project.getDescription());

        long id = mDatabase.insert(getTable(), null, values);
        return find(id);
    }
}
