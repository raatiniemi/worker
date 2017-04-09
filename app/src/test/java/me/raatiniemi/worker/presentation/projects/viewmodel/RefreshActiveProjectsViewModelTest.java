/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.presentation.projects.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RefreshActiveProjectsViewModelTest {
    private TestSubscriber<List<Integer>> positionsForActiveProjects;

    private RefreshActiveProjectsViewModel.ViewModel vm;

    private ProjectsItem getProjectsItem(boolean isActive) {
        ProjectsItem projectsItem = mock(ProjectsItem.class);
        when(projectsItem.isActive())
                .thenReturn(isActive);

        return projectsItem;
    }

    @Before
    public void setUp() {
        positionsForActiveProjects = new TestSubscriber<>();

        vm = new RefreshActiveProjectsViewModel.ViewModel();
    }

    @Test
    public void positionsForActiveProjects_withoutProjects() {
        vm.output().positionsForActiveProjects()
                .subscribe(positionsForActiveProjects);

        vm.input().projects(Collections.emptyList());

        positionsForActiveProjects.assertValue(Collections.emptyList());
        positionsForActiveProjects.assertNotCompleted();
    }

    @Test
    public void positionsForActiveProjects_withoutActiveProjects() throws DomainException {
        vm.output().positionsForActiveProjects()
                .subscribe(positionsForActiveProjects);

        vm.input().projects(Collections.singletonList(getProjectsItem(false)));

        positionsForActiveProjects.assertValue(Collections.emptyList());
        positionsForActiveProjects.assertNotCompleted();
    }

    @Test
    public void positionsForActiveProjects_withActiveProject() throws DomainException {
        vm.output().positionsForActiveProjects()
                .subscribe(positionsForActiveProjects);

        vm.input().projects(Arrays.asList(getProjectsItem(false), getProjectsItem(true)));

        positionsForActiveProjects.assertValue(Collections.singletonList(1));
        positionsForActiveProjects.assertNotCompleted();
    }
}
