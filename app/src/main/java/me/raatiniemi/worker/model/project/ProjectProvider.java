package me.raatiniemi.worker.model.project;

import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import rx.Observable;
import rx.functions.Func0;

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
}
