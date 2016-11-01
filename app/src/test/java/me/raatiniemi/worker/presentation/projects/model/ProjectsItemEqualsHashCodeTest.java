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
import static me.raatiniemi.util.NullUtil.isNull;

@RunWith(Parameterized.class)
public class ProjectsItemEqualsHashCodeTest {
    private String message;
    private Boolean expected;
    private ProjectsItem projectsItem;
    private Object compareTo;

    public ProjectsItemEqualsHashCodeTest(
            String message,
            Boolean expected,
            ProjectsItem projectsItem,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.projectsItem = projectsItem;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws InvalidProjectNameException {
        Project project = new Project.Builder("Name")
                .id(1L)
                .build();
        ProjectsItem projectsItem = new ProjectsItem(project);
        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                projectsItem,
                                projectsItem
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                projectsItem,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                projectsItem,
                                ""
                        },
                        {
                                "With different project",
                                Boolean.FALSE,
                                projectsItem,
                                new ProjectsItem(
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
        assertTrue(message, projectsItem.equals(compareTo));

        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, projectsItem.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, projectsItem.equals(compareTo));

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return;
        }

        assertFalse(message, projectsItem.hashCode() == compareTo.hashCode());
    }
}
