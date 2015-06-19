package me.raatiniemi.worker.projects;

import me.raatiniemi.worker.model.project.Project;

/**
 * Methods related to handling of the project view.
 */
public interface ProjectsView {
    void createNewProject();

    void updateProject(Project project);
}
