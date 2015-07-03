package me.raatiniemi.worker.model.project;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.model.time.TimeMapper;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimesheetItem;
import me.raatiniemi.worker.provider.WorkerContract;
import me.raatiniemi.worker.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.provider.WorkerContract.TimeContract;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimeGroup;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

public class ProjectProvider {
    /**
     * Context used with the project provider.
     */
    private Context mContext;

    /**
     * Constructor.
     *
     * @param context Context used with the project provider.
     */
    public ProjectProvider(Context context) {
        mContext = context;
    }

    /**
     * Get the context.
     *
     * @return Context used with the project provider.
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * Retrieve the projects.
     *
     * @return Observable emitting the projects.
     */
    public Observable<List<Project>> getProjects() {
        return Observable.defer(new Func0<Observable<List<Project>>>() {
            @Override
            public Observable<List<Project>> call() {
                List<Project> projects = new ArrayList<>();

                Cursor cursor = getContext().getContentResolver()
                    .query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                    );
                if (cursor.moveToFirst()) {
                    do {
                        projects.add(ProjectMapper.map(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                return Observable.just(projects);
            }
        });
    }

    /**
     * Retrieve project based on the project id.
     *
     * @param id Id for the project.
     * @return Observable emitting the project.
     */
    public Observable<Project> getProject(final Long id) {
        return Observable.defer(new Func0<Observable<Project>>() {
            @Override
            public Observable<Project> call() {
                Project project = null;

                Cursor cursor = getContext().getContentResolver().query(
                    ProjectContract.getItemUri(String.valueOf(id)),
                    ProjectContract.COLUMNS,
                    null,
                    null,
                    null
                );
                if (cursor.moveToFirst()) {
                    project = ProjectMapper.map(cursor);
                }
                cursor.close();

                return Observable.just(project);
            }
        });
    }

    /**
     * Create new project.
     *
     * @param project New project to create.
     * @return Observable emitting the new project.
     */
    public Observable<Project> createProject(final Project project) {
        return Observable.defer(new Func0<Observable<Project>>() {
            @Override
            public Observable<Project> call() {
                try {
                    Uri uri = getContext().getContentResolver()
                        .insert(
                            ProjectContract.getStreamUri(),
                            ProjectMapper.map(project)
                        );

                    return getProject(Long.valueOf(ProjectContract.getItemId(uri)));
                } catch (Throwable e) {
                    return Observable.error(new ProjectAlreadyExistsException());
                }
            }
        });
    }

    /**
     * Clock in or clock out the project at given date.
     *
     * @param project Project to clock in/out.
     * @param date Date to clock in/out at.
     * @return Observable emitting the clocked in/out project.
     */
    public Observable<Project> clockActivityChange(final Project project, final Date date) {
        try {
            // Depending on whether the project is active we have
            // to clock in or clock out at the given date.
            Observable<Long> observable;
            if (!project.isActive()) {
                observable = clockIn(project.clockInAt(date));
            } else {
                observable = clockOut(project.clockOutAt(date));
            }

            // With the emitted project id, retrieve the project with time.
            return observable.flatMap(new Func1<Long, Observable<Project>>() {
                @Override
                public Observable<Project> call(Long projectId) {
                    return getProject(projectId)
                        .map(new Func1<Project, Project>() {
                            @Override
                            public Project call(Project project) {
                                return getTime(project);
                            }
                        });
                }
            });
        } catch (DomainException e) {
            return Observable.error(e);
        }
    }

    /**
     * Populate project with registered time.
     *
     * @param project Project to populate with registered time.
     * @return Project populated with registered time.
     */
    public Project getTime(final Project project) {
        // If the project id is null then it's a new project,
        // i.e. it will not have any registered time.
        if (null == project.getId()) {
            return project;
        }

        List<Time> result = new ArrayList<>();

        // Reset the calendar to retrieve timestamp
        // of the beginning of the month.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Cursor cursor = getContext().getContentResolver()
            .query(
                ProjectContract.getItemTimeUri(String.valueOf(project.getId())),
                TimeContract.COLUMNS,
                TimeContract.START + ">=? OR " + TimeContract.STOP + " = 0",
                new String[]{ String.valueOf(calendar.getTimeInMillis()) },
                ProjectContract.ORDER_BY_TIME
            );
        if (cursor.moveToFirst()) {
            do {
                Time time = TimeMapper.map(cursor);
                if (null != time) {
                    result.add(time);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        project.addTime(result);
        return project;
    }

    /**
     * Retrieve time based on the time id.
     *
     * @param id Id for the time.
     * @return Observable emitting the time.
     */
    public Observable<Time> getTime(final Long id) {
        return Observable.defer(new Func0<Observable<Time>>() {
            @Override
            public Observable<Time> call() {
                Time time = null;

                Cursor cursor = mContext.getContentResolver().query(
                    TimeContract.getItemUri(String.valueOf(id)),
                    TimeContract.COLUMNS,
                    null,
                    null,
                    null
                );
                if (cursor.moveToFirst()) {
                    time = TimeMapper.map(cursor);
                }
                cursor.close();

                return Observable.just(time);
            }
        });
    }

    /**
     * Add time item.
     *
     * @param time Time item to add.
     * @return Observable emitting the created time item.
     */
    public Observable<Time> add(final Time time) {
        return Observable.defer(new Func0<Observable<Time>>() {
            @Override
            public Observable<Time> call() {
                Uri uri = getContext().getContentResolver()
                    .insert(
                        TimeContract.getStreamUri(),
                        TimeMapper.map(time)
                    );

                return getTime(Long.valueOf(TimeContract.getItemId(uri)));
            }
        });
    }

    public Observable<Time> remove(final Time time) {
        return Observable.defer(new Func0<Observable<Time>>() {
            @Override
            public Observable<Time> call() {
                getContext().getContentResolver()
                    .delete(
                        TimeContract.getItemUri(String.valueOf(time.getId())),
                        null,
                        null
                    );

                return Observable.just(time);
            }
        });
    }

    /**
     * Update time item.
     *
     * @param time Time item to update.
     * @return Observable emitting the updated time item.
     */
    public Observable<Time> update(final Time time) {
        return Observable.defer(new Func0<Observable<Time>>() {
            @Override
            public Observable<Time> call() {
                getContext().getContentResolver()
                    .update(
                        TimeContract.getItemUri(String.valueOf(time.getId())),
                        TimeMapper.map(time),
                        null,
                        null
                    );

                return getTime(time.getId());
            }
        });
    }

    /**
     * Clock in the project.
     *
     * @param time Clock in time for project.
     * @return Observable emitting the id for the project.
     */
    private Observable<Long> clockIn(final Time time) {
        return add(time)
            .flatMap(new Func1<Time, Observable<Long>>() {
                @Override
                public Observable<Long> call(Time time) {
                    return Observable.just(time.getProjectId());
                }
            });
    }

    /**
     * Clock out the project.
     *
     * @param time Clock out time for project.
     * @return Observable emitting the id for the project.
     */
    private Observable<Long> clockOut(final Time time) {
        return update(time)
            .flatMap(new Func1<Time, Observable<Long>>() {
                @Override
                public Observable<Long> call(Time time) {
                    return Observable.just(time.getProjectId());
                }
            });
    }

    public Observable<List<TimesheetItem>> getTimesheet(final Long id, final int offset) {
        return Observable.defer(new Func0<Observable<List<TimesheetItem>>>() {
            @Override
            public Observable<List<TimesheetItem>> call() {
                List<TimesheetItem> items = new ArrayList<>();

                // TODO: Simplify the builing of the URI with query parameters.
                Uri uri = ProjectContract.getItemTimesheetUri(String.valueOf(id))
                    .buildUpon()
                    .appendQueryParameter(WorkerContract.QUERY_PARAMETER_OFFSET, String.valueOf(offset))
                    .appendQueryParameter(WorkerContract.QUERY_PARAMETER_LIMIT, "10")
                    .build();

                Cursor cursor = mContext.getContentResolver()
                    .query(
                        uri,
                        ProjectContract.COLUMNS_TIMESHEET,
                        null,
                        null,
                        ProjectContract.ORDER_BY_TIMESHEET
                    );
                if (cursor.moveToFirst()) {
                    do {
                        // We're getting the id for the time objects as a comma-separated string column.
                        // We have to split the value before attempting to retrieve each individual row.
                        String grouped = cursor.getString(1);
                        String[] rows = grouped.split(",");
                        if (0 < rows.length) {
                            // Instantiate the group. The first column should be
                            // the lowest timestamp within the interval.
                            TimeGroup group = new TimeGroup(
                                (cursor.getPosition() + offset),
                                new Date(cursor.getLong(0))
                            );
                            TimesheetItem item = new TimesheetItem(group);

                            for (String id : rows) {
                                Cursor row = mContext.getContentResolver()
                                    .query(
                                        TimeContract.getItemUri(id),
                                        TimeContract.COLUMNS,
                                        null,
                                        null,
                                        null
                                    );
                                if (row.moveToFirst()) {
                                    do {
                                        Time time = TimeMapper.map(row);
                                        if (null != time) {
                                            item.add(time);
                                        }
                                    } while (row.moveToNext());
                                }
                                row.close();
                            }

                            // Reverse the order of the children to put the latest
                            // item at the top of the list.
                            Collections.reverse(item);
                            items.add(item);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();

                return Observable.just(items);
            }
        });
    }
}
