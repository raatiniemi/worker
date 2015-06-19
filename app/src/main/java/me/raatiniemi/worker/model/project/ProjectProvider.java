package me.raatiniemi.worker.model.project;

import java.util.Date;

import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.model.time.Time;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

public class ProjectProvider {
    public Observable<ProjectCollection> getProjects() {
        return Observable.defer(new Func0<Observable<ProjectCollection>>() {
            @Override
            public Observable<ProjectCollection> call() {
                ProjectMapper mapper = MapperRegistry.getProjectMapper();
                return Observable.just(mapper.getProjects());
            }
        });
    }

    public Observable<Project> getProject(final Long id) {
        return Observable.defer(new Func0<Observable<Project>>() {
            @Override
            public Observable<Project> call() {
                ProjectMapper mapper = MapperRegistry.getProjectMapper();
                return Observable.just(mapper.find(id));
            }
        });
    }

    public Observable<Project> clockActivityChange(final Project project, final Date date) {
        try {
            Observable<Long> observable;
            if (!project.isActive()) {
                observable = clockIn(project.clockInAt(date));
            } else {
                observable = clockOut(project.clockOutAt(date));
            }

            return observable.flatMap(new Func1<Long, Observable<Project>>() {
                @Override
                public Observable<Project> call(Long projectId) {
                    ProjectMapper mapper = MapperRegistry.getProjectMapper();
                    return Observable.just(mapper.reload(projectId));
                }
            });
        } catch (DomainException e) {
            return Observable.error(e);
        }
    }

    private Observable<Long> clockIn(final Time time) {
        return Observable.defer(new Func0<Observable<Long>>() {
            @Override
            public Observable<Long> call() {
                TimeMapper mapper = MapperRegistry.getTimeMapper();
                mapper.insert(time);

                return Observable.just(time.getProjectId());
            }
        });
    }

    private Observable<Long> clockOut(final Time time) {
        return Observable.defer(new Func0<Observable<Long>>() {
            @Override
            public Observable<Long> call() {
                TimeMapper mapper = MapperRegistry.getTimeMapper();
                mapper.update(time);

                return Observable.just(time.getProjectId());
            }
        });
    }
}
