package me.raatiniemi.worker.projects;

import me.raatiniemi.worker.model.project.Project;

/**
 * Methods related to handling of the project view.
 */
public interface ProjectsView {
    /**
     * Add project to the list.
     *
     * @param project Project to add to the list.
     */
    void addProject(Project project);

    /**
     * Update project in the list.
     *
     * @param project Project to update in the list.
     */
    void updateProject(Project project);

    /**
     * Open the dialog for creating a new project.
     */
    void createNewProject();
}
