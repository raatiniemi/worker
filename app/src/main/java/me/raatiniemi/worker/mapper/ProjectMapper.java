package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.provider.WorkerContract.*;
import me.raatiniemi.worker.provider.WorkerDatabase.*;
import me.raatiniemi.worker.util.ProjectCollection;
import me.raatiniemi.worker.util.TimeCollection;

public class ProjectMapper extends AbstractMapper<Project>
{
    private TimeMapper mTimeMapper;

    public ProjectMapper(TimeMapper timeMapper)
    {
        super();

        mTimeMapper = timeMapper;
    }

    /**
     * Retrieve the name of the project table within the database.
     * @return Name of project table.
     */
    protected String getTable()
    {
        return Tables.PROJECT;
    }

    /**
     * Retrieve the project table columns.
     * @return Project table columns.
     */
    protected String[] getColumns()
    {
        return new String[]{
            ProjectColumns.ID,
            ProjectColumns.NAME,
            ProjectColumns.DESCRIPTION,
            ProjectColumns.ARCHIVED
        };
    }

    /**
     * Load the project object from the cursor.
     * @param row Database cursor.
     * @return Project with data.
     */
    protected Project load(Cursor row)
    {
        // Retrieve the project data from the cursor.
        long id = row.getLong(row.getColumnIndex(ProjectColumns.ID));
        String name = row.getString(row.getColumnIndex(ProjectColumns.NAME));
        String description = row.getString(row.getColumnIndex(ProjectColumns.DESCRIPTION));
        long archived = row.getLong(row.getColumnIndex(ProjectColumns.ARCHIVED));

        // Initialize the project with data from the cursor.
        Project project = new Project(id, name);
        project.setDescription(description);
        project.setArchived(archived);

        // If the mapper for time objects is available, we should load
        // the the project time for the default interval.
        if (null != mTimeMapper) {
            TimeCollection time = mTimeMapper.findTimeByProject(project);
            for (Time item: time) {
                project.addTime(item);
            }
        }

        return project;
    }

    /**
     * Retrieve all of the available projects.
     * @return List of all available projects.
     */
    public ProjectCollection getProjects()
    {
        ProjectCollection result = new ProjectCollection();

        // TODO: Exclude projects that has been archived.
        Cursor rows = mDatabase.query(getTable(), getColumns(), null, null, null, null, null);
        if (rows.moveToFirst()) {
            do {
                Project project = load(rows);
                result.add(project);
            } while (rows.moveToNext());
        }

        return result;
    }

    /**
     * Find project by name.
     * @param name Name of the project to find.
     * @return Project if found, otherwise null.
     */
    public Project find(String name)
    {
        // Build the selection to find the project by name.
        String selection = ProjectColumns.NAME + "=?";
        String[] selectionArgs = new String[]{name};

        Cursor row = mDatabase.query(getTable(), getColumns(), selection, selectionArgs, null, null, null);
        if (!row.moveToFirst()) {
            return null;
        }

        return load(row);
    }

    /**
     * Attempt to save new project.
     * @param project Project to be saved.
     * @return Newly saved project.
     * @throws ProjectAlreadyExistsException If the project name already exists.
     */
    public Project insert(Project project) throws ProjectAlreadyExistsException
    {
        // Verify that the project name is unique.
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
