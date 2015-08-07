package me.raatiniemi.worker.model.domain.project;

import java.util.Comparator;

public class ProjectComparator implements Comparator<Project> {
    @Override
    public int compare(Project lhs, Project rhs) {
        return lhs.getId().compareTo(rhs.getId());
    }
}