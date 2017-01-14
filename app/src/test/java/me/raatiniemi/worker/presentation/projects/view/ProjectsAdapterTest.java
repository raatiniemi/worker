/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.projects.view;

import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ProjectsAdapterTest {
    private ProjectsAdapter adapter;

    @Before
    public void setUp() {
        Resources resources = mock(Resources.class);
        OnProjectActionListener listener = mock(OnProjectActionListener.class);

        adapter = new ProjectsAdapter(resources, listener, mock(HintedImageButtonListener.class));
    }

    @Test
    public void findProject_withoutAvailableProject() throws InvalidProjectNameException {
        adapter.add(buildProjectsItem(1L, "Project #1"));

        int position = adapter.findProject(buildProjectsItem(2L, "Project #2"));

        assertEquals(RecyclerView.NO_POSITION, position);
    }

    @Test
    public void findProject_withProject() throws InvalidProjectNameException {
        adapter.add(buildProjectsItem(1L, "Project #1"));

        int position = adapter.findProject(buildProjectsItem(1L, "Project #1"));

        assertEquals(0, position);
    }

    @Test
    public void findProject_withProjects() throws InvalidProjectNameException {
        adapter.add(buildProjectsItem(1L, "Project #1"));
        adapter.add(buildProjectsItem(2L, "Project #2"));
        adapter.add(buildProjectsItem(3L, "Project #3"));
        adapter.add(buildProjectsItem(4L, "Project #4"));
        adapter.add(buildProjectsItem(5L, "Project #5"));
        adapter.add(buildProjectsItem(6L, "Project #6"));

        int position = adapter.findProject(buildProjectsItem(4L, "Project #4"));

        assertEquals(3, position);
    }

    private ProjectsItem buildProjectsItem(long projectId, String projectName)
            throws InvalidProjectNameException {

        Project project = new Project.Builder(projectName)
                .id(projectId)
                .build();

        return new ProjectsItem(project);
    }
}
