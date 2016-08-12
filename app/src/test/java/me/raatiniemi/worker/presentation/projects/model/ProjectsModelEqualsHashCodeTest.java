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

package me.raatiniemi.worker.presentation.projects.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ProjectsModelEqualsHashCodeTest {
    private String message;
    private Boolean expected;
    private ProjectsModel projectsModel;
    private Object compareTo;

    public ProjectsModelEqualsHashCodeTest(
            String message,
            Boolean expected,
            ProjectsModel projectsModel,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.projectsModel = projectsModel;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> parameters()
            throws InvalidProjectNameException {
        Project project = new Project.Builder("Name")
                .id(1L)
                .build();
        ProjectsModel projectsModel = new ProjectsModel(project);
        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                projectsModel,
                                projectsModel
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                projectsModel,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                projectsModel,
                                ""
                        },
                        {
                                "With different project",
                                Boolean.FALSE,
                                projectsModel,
                                new ProjectsModel(
                                        new Project.Builder("Project name")
                                                .id(2L)
                                                .build()
                                )
                        }
                }
        );
    }

    @Test
    public void equals() {
        if (shouldBeEqual()) {
            assertEqual();
            return;
        }

        assertNotEqual();
    }

    private Boolean shouldBeEqual() {
        return expected;
    }

    private void assertEqual() {
        assertTrue(message, projectsModel.equals(compareTo));

        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, projectsModel.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, projectsModel.equals(compareTo));

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (null == compareTo) {
            return;
        }

        assertFalse(message, projectsModel.hashCode() == compareTo.hashCode());
    }
}
