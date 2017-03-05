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

package me.raatiniemi.worker.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static me.raatiniemi.util.NullUtil.isNull;

@RunWith(Parameterized.class)
public class ProjectEqualsHashCodeTest {
    private String message;
    private Boolean expected;
    private Project project;
    private Object compareTo;

    public ProjectEqualsHashCodeTest(
            String message,
            Boolean expected,
            Project project,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.project = project;
        this.compareTo = compareTo;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws DomainException {
        Project project = Project.builder("Project name")
                .id(1L)
                .build();

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                project,
                                project
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                project,
                                null
                        },
                        {
                                "With incompatible object",
                                Boolean.FALSE,
                                project,
                                ""
                        },
                        {
                                "With different project name",
                                Boolean.FALSE,
                                project,
                                Project.builder("Name")
                                        .id(1L)
                                        .build()
                        },
                        {
                                "With different id",
                                Boolean.FALSE,
                                project,
                                Project.builder("Project name")
                                        .id(2L)
                                        .build()
                        },
                        {
                                "With different registered time",
                                Boolean.FALSE,
                                project,
                                buildProjectWithRegisteredTime()
                        }
                }
        );
    }

    private static Project buildProjectWithRegisteredTime()
            throws DomainException {
        Project project = Project.builder("Project name")
                .id(1L)
                .build();

        Time time = Time.builder(1L)
                .startInMilliseconds(1L)
                .stopInMilliseconds(2L)
                .build();

        List<Time> registeredTime = new ArrayList<>();
        registeredTime.add(time);

        project.addTime(registeredTime);
        return project;
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
        assertTrue(message, project.equals(compareTo));

        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertTrue(message, project.hashCode() == compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertFalse(message, project.equals(compareTo));

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return;
        }

        assertFalse(message, project.hashCode() == compareTo.hashCode());
    }
}
