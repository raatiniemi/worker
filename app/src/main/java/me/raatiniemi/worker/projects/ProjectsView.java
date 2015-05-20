package me.raatiniemi.worker.projects;

import me.raatiniemi.worker.domain.Project;

/**
 * Methods related to handling of the project view.
 */
public interface ProjectsView {
    /**
     * Add new project to the view.
     *
     * @param project Project to add.
     */
    void addProject(Project project);
}
