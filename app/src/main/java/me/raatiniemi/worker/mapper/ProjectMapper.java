package me.raatiniemi.worker.mapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;

import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.model.project.Project;
import me.raatiniemi.worker.model.project.ProjectCollection;
import me.raatiniemi.worker.provider.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.provider.WorkerContract.Tables;

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
        return ProjectContract.COLUMNS;
    }

    /**
     * Load the project object from the cursor.
     *
     * @param row Database cursor.
     * @return Project with data.
     */
    protected Project load(Cursor row) {
        return map(row);
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
            project.addTime(mTimeMapper.findTimeByProject(project));
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
        Cursor rows = mContext.getContentResolver().query(ProjectContract.getStreamUri(), getColumns(), selection, null, null);
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
            ProjectContract.getItemUri(String.valueOf(id)),
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
     * Attempt to save new project.
     *
     * @param project Project to be saved.
     * @return Newly saved project.
     * @throws ProjectAlreadyExistsException If the project name already exists.
     */
    public Project insert(Project project) throws ProjectAlreadyExistsException {
        try {
            ContentValues values = new ContentValues();
            values.put(ProjectColumns.NAME, project.getName());
            values.put(ProjectColumns.DESCRIPTION, project.getDescription());

            Uri uri = mContext.getContentResolver().insert(ProjectContract.getStreamUri(), values);
            return find(Long.valueOf(ProjectContract.getItemId(uri)));
        } catch (SQLiteConstraintException e) {
            throw new ProjectAlreadyExistsException();
        }
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

    /**
     * Map project from cursor.
     *
     * @param cursor Cursor with data to map to Project.
     * @return Project with data from cursor.
     */
    public static Project map(Cursor cursor) {
        // Map the id and name for the project.
        long id = cursor.getLong(cursor.getColumnIndex(ProjectColumns.ID));
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
}
