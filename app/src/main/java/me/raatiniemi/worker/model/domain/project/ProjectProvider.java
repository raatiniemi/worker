/*
 * Copyright (C) 2015 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.model.domain.project;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.model.domain.time.Time;
import me.raatiniemi.worker.model.domain.time.TimeMapper;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimesheetItem;
import me.raatiniemi.worker.provider.WorkerContract;
import me.raatiniemi.worker.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.provider.WorkerContract.TimeContract;
import rx.Observable;
import rx.android.content.ContentObservable;
import rx.functions.Action1;
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
        return Observable.defer(new Func0<Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call() {
                Cursor cursor = getContext().getContentResolver()
                        .query(
                                ProjectContract.getStreamUri(),
                                ProjectContract.COLUMNS,
                                null,
                                null,
                                null
                        );

                return ContentObservable.fromCursor(cursor);
            }
        }).map(new Func1<Cursor, Project>() {
            @Override
            public Project call(Cursor cursor) {
                return ProjectMapper.map(cursor);
            }
        }).toList();
    }

    /**
     * Retrieve project based on the project id.
     *
     * @param id Id for the project.
     * @return Observable emitting the project.
     */
    public Observable<Project> getProject(final Long id) {
        return Observable.just(String.valueOf(id))
                .flatMap(new Func1<String, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(String id) {
                        Cursor cursor = getContext().getContentResolver().query(
                                ProjectContract.getItemUri(id),
                                ProjectContract.COLUMNS,
                                null,
                                null,
                                null
                        );

                        return ContentObservable.fromCursor(cursor);
                    }
                })
                .map(new Func1<Cursor, Project>() {
                    @Override
                    public Project call(Cursor cursor) {
                        return ProjectMapper.map(cursor);
                    }
                })
                .first();
    }

    /**
     * Create new project.
     *
     * @param project New project to create.
     * @return Observable emitting the new project.
     */
    public Observable<Project> createProject(final Project project) {
        return Observable.just(project)
                .map(new Func1<Project, ContentValues>() {
                    @Override
                    public ContentValues call(Project project) {
                        return ProjectMapper.map(project);
                    }
                })
                .flatMap(new Func1<ContentValues, Observable<String>>() {
                    @Override
                    public Observable<String> call(ContentValues values) {
                        try {
                            Uri uri = getContext().getContentResolver()
                                    .insert(
                                            ProjectContract.getStreamUri(),
                                            values
                                    );

                            return Observable.just(ProjectContract.getItemId(uri));
                        } catch (Throwable e) {
                            return Observable.error(new ProjectAlreadyExistsException());
                        }
                    }
                })
                .map(new Func1<String, Long>() {
                    @Override
                    public Long call(String id) {
                        return Long.valueOf(id);
                    }
                })
                .flatMap(new Func1<Long, Observable<Project>>() {
                    @Override
                    public Observable<Project> call(Long id) {
                        return getProject(id);
                    }
                });
    }

    /**
     * Delete project with registered time.
     *
     * @param project Project to be deleted.
     * @return Observable emitting the deleted project.
     */
    public Observable<Project> deleteProject(final Project project) {
        return Observable.just(project)
                .flatMap(new Func1<Project, Observable<Project>>() {
                    @Override
                    public Observable<Project> call(Project project) {
                        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

                        // Add operation for removing the registered time for the
                        // project. The operation have to be performed before the
                        // actual project deletion.
                        Uri uri = ProjectContract.getItemTimeUri(project.getId());
                        batch.add(
                                ContentProviderOperation.newDelete(uri)
                                        .build()
                        );

                        // Add operation for removing the project.
                        uri = ProjectContract.getItemUri(project.getId());
                        batch.add(
                                ContentProviderOperation.newDelete(uri)
                                        .build()
                        );

                        try {
                            // Attempt to remove the registered time and project
                            // within a single transactional operation.
                            getContext().getContentResolver()
                                    .applyBatch(WorkerContract.AUTHORITY, batch);
                        } catch (RemoteException e) {
                            return Observable.error(e);
                        } catch (OperationApplicationException e) {
                            return Observable.error(e);
                        }

                        return Observable.just(project);
                    }
                });
    }

    /**
     * Clock in or clock out the project at given date.
     *
     * @param project Project to clock in/out.
     * @param date    Date to clock in/out at.
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
                    return getProject(projectId);
                }
            }).map(new Func1<Project, Project>() {
                @Override
                public Project call(Project project) {
                    return getTime(project);
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

        final List<Time> result = new ArrayList<>();

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
                        new String[]{String.valueOf(calendar.getTimeInMillis())},
                        ProjectContract.ORDER_BY_TIME
                );

        ContentObservable.fromCursor(cursor)
                .map(new Func1<Cursor, Time>() {
                    @Override
                    public Time call(Cursor cursor) {
                        return TimeMapper.map(cursor);
                    }
                })
                .filter(new Func1<Time, Boolean>() {
                    @Override
                    public Boolean call(Time time) {
                        return null != time;
                    }
                })
                .subscribe(new Action1<Time>() {
                    @Override
                    public void call(Time time) {
                        result.add(time);
                    }
                });

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
        return Observable.just(String.valueOf(id))
                .flatMap(new Func1<String, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(String id) {
                        Cursor cursor = getContext().getContentResolver()
                                .query(
                                        TimeContract.getItemUri(id),
                                        TimeContract.COLUMNS,
                                        null,
                                        null,
                                        null
                                );

                        return ContentObservable.fromCursor(cursor);
                    }
                })
                .map(new Func1<Cursor, Time>() {
                    @Override
                    public Time call(Cursor cursor) {
                        return TimeMapper.map(cursor);
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
        return Observable.just(time)
                .map(new Func1<Time, ContentValues>() {
                    @Override
                    public ContentValues call(Time time) {
                        return TimeMapper.map(time);
                    }
                })
                .map(new Func1<ContentValues, String>() {
                    @Override
                    public String call(ContentValues values) {
                        Uri uri = getContext().getContentResolver()
                                .insert(
                                        TimeContract.getStreamUri(),
                                        values
                                );

                        return TimeContract.getItemId(uri);
                    }
                })
                .map(new Func1<String, Long>() {
                    @Override
                    public Long call(String id) {
                        return Long.valueOf(id);
                    }
                })
                .flatMap(new Func1<Long, Observable<Time>>() {
                    @Override
                    public Observable<Time> call(Long id) {
                        return getTime(id);
                    }
                });
    }

    public Observable<Time> remove(final Time time) {
        return Observable.just(time)
                .map(new Func1<Time, String>() {
                    @Override
                    public String call(Time time) {
                        return String.valueOf(time.getId());
                    }
                })
                .map(new Func1<String, Time>() {
                    @Override
                    public Time call(String id) {
                        getContext().getContentResolver()
                                .delete(
                                        TimeContract.getItemUri(id),
                                        null,
                                        null
                                );

                        return time;
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
                .map(new Func1<Time, Long>() {
                    @Override
                    public Long call(Time time) {
                        return time.getProjectId();
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
                .map(new Func1<Time, Long>() {
                    @Override
                    public Long call(Time time) {
                        return time.getProjectId();
                    }
                });
    }

    public Observable<List<TimesheetItem>> getTimesheet(final Long id, final int offset) {
        // TODO: Simplify the builing of the URI with query parameters.
        Uri uri = ProjectContract.getItemTimesheetUri(String.valueOf(id))
                .buildUpon()
                .appendQueryParameter(WorkerContract.QUERY_PARAMETER_OFFSET, String.valueOf(offset))
                .appendQueryParameter(WorkerContract.QUERY_PARAMETER_LIMIT, "10")
                .build();

        return Observable.just(uri)
                .flatMap(new Func1<Uri, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(Uri uri) {
                        Cursor cursor = getContext().getContentResolver()
                                .query(
                                        uri,
                                        ProjectContract.COLUMNS_TIMESHEET,
                                        null,
                                        null,
                                        ProjectContract.ORDER_BY_TIMESHEET
                                );

                        return ContentObservable.fromCursor(cursor);
                    }
                })
                .map(new Func1<Cursor, TimesheetItem>() {
                    @Override
                    public TimesheetItem call(Cursor cursor) {
                        final TimesheetItem item = new TimesheetItem(
                                new Date(cursor.getLong(0))
                        );

                        // We're getting the id for the time objects as a comma-separated string column.
                        // We have to split the value before attempting to retrieve each individual row.
                        String grouped = cursor.getString(1);
                        String[] rows = grouped.split(",");
                        if (0 < rows.length) {
                            for (String id : rows) {
                                getTime(Long.valueOf(id))
                                        .filter(new Func1<Time, Boolean>() {
                                            @Override
                                            public Boolean call(Time time) {
                                                return time != null;
                                            }
                                        })
                                        .subscribe(new Action1<Time>() {
                                            @Override
                                            public void call(Time time) {
                                                item.add(time);
                                            }
                                        });
                            }

                            // Reverse the order of the children to put the latest
                            // item at the top of the list.
                            Collections.reverse(item);
                        }
                        return item;
                    }
                })
                .toList();
    }
}
