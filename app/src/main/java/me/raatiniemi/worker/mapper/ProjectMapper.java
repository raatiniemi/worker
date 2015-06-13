package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import me.raatiniemi.worker.model.project.Project;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.provider.WorkerContract.Projects;
import me.raatiniemi.worker.provider.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.provider.WorkerContract.Tables;
import me.raatiniemi.worker.model.project.ProjectCollection;
import me.raatiniemi.worker.model.time.TimeCollection;

public class ProjectMapper extends AbstractMapper<Project> {
    private static final String TAG = "ProjectMapper";

    private Context mContext;

    private TimeMapper mTimeMapper;

    public ProjectMapper(Context context, TimeMapper timeMapper) {
        super();

        mContext = context;
        mTimeMapper = timeMapper;
    }

    /**
     * Retrieve the name of the project table within the database.
     *
     * @return Name of project table.
     */
    protected String getTable() {
        return Tables.PROJECT;
    }

    /**
     * Retrieve the project table columns.
     *
     * @return Project table columns.
     */
    protected String[] getColumns() {
        return new String[]{
            ProjectColumns.ID,
            ProjectColumns.NAME,
            ProjectColumns.DESCRIPTION,
            ProjectColumns.ARCHIVED
        };
    }

    /**
     * Load the project object from the cursor.
     *
     * @param row Database cursor.
     * @return Project with data.
     */
    protected Project load(Cursor row) {
        // Retrieve the project data from the cursor.
        long id = row.getLong(row.getColumnIndex(ProjectColumns.ID));
        String name = row.getString(row.getColumnIndex(ProjectColumns.NAME));
        String description = row.getString(row.getColumnIndex(ProjectColumns.DESCRIPTION));
        long archived = row.getLong(row.getColumnIndex(ProjectColumns.ARCHIVED));

        // Initialize the project with data from the cursor.
        Project project = new Project(id, name);
        project.setDescription(description);
        project.setArchived(archived);

        return project;
    }

    /**
     * Load the time for the project.
     *
     * @param project Project for which to load the time.
     */
    private void loadTime(Project project) {
        // If the mapper for time objects is available, we should load
        // the the project time for the default interval.
        if (null != mTimeMapper) {
            TimeCollection time = mTimeMapper.findTimeByProject(project);
            for (Time item : time) {
                project.addTime(item);
            }
        }
    }

    /**
     * Retrieve all of the available projects.
     *
     * @return List of all available projects.
     */
    public ProjectCollection getProjects() {
        ProjectCollection result = new ProjectCollection();

        // Exclude projects that have been archived.
        String selection = "COALESCE(" + ProjectColumns.ARCHIVED + ", 0) = 0";
        Cursor rows = mContext.getContentResolver().query(Projects.CONTENT_URI, getColumns(), selection, null, null);
        if (rows.moveToFirst()) {
            do {
                Project project = load(rows);

                // Populate the project with the registered time.
                loadTime(project);
                result.add(project);
            } while (rows.moveToNext());
        }
        rows.close();

        return result;
    }

    public Project find(long id) {
        Project project = null;

        Cursor row = mContext.getContentResolver().query(
            Projects.buildUri(String.valueOf(id)),
            getColumns(),
            null,
            null,
            null
        );
        if (row.moveToFirst()) {
            project = load(row);
        }
        row.close();

        return project;
    }

    /**
     * Find project by name.
     *
     * @param name Name of the project to find.
     * @return Project if found, otherwise null.
     */
    public Project find(String name) {
        // Build the selection to find the project by name.
        String selection = ProjectColumns.NAME + "=?";
        String[] selectionArgs = new String[]{ name };

        Cursor row = mDatabase.query(getTable(), getColumns(), selection, selectionArgs, null, null, null);
        if (!row.moveToFirst()) {
            return null;
        }

        return load(row);
    }

    /**
     * Attempt to save new project.
     *
     * @param project Project to be saved.
     * @return Newly saved project.
     * @throws ProjectAlreadyExistsException If the project name already exists.
     */
    public Project insert(Project project) throws ProjectAlreadyExistsException {
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

    /**
     * Reload the project, and re-populate it with the registered time.
     *
     * @param id Id for the project to reload.
     * @return Project with reloaded data.
     */
    public Project reload(long id) {
        Project project = find(id);
        if (null != project) {
            loadTime(project);
        }

        return project;
    }
}
