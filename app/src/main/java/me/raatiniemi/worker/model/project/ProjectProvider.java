package me.raatiniemi.worker.model.project;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.model.time.TimeCollection;
import me.raatiniemi.worker.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.provider.WorkerContract.TimeContract;
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
    public Observable<ProjectCollection> getProjects() {
        return Observable.defer(new Func0<Observable<ProjectCollection>>() {
            @Override
            public Observable<ProjectCollection> call() {
                ProjectCollection projects = new ProjectCollection();

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
        }).map(new Func1<ProjectCollection, ProjectCollection>() {
            @Override
            public ProjectCollection call(ProjectCollection projects) {
                // Populate the projects with the registered time.
                for (Project project : projects) {
                    int index = projects.indexOf(project);
                    projects.set(index, getTime(project));
                }

                return projects;
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
    private Project getTime(final Project project) {
        TimeMapper mapper = MapperRegistry.getTimeMapper();
        TimeCollection time = mapper.findTimeByProject(project);
        if (null != time) {
            project.addTime(time);
        }
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
                TimeMapper mapper = MapperRegistry.getTimeMapper();
                return Observable.just(mapper.find(id));
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
}
