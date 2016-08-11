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

package me.raatiniemi.worker.domain.comparator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ProjectComparatorTest {
    private final ProjectComparator comparator = new ProjectComparator();

    private Project createProject(long id)
            throws InvalidProjectNameException {
        return new Project.Builder("Project name")
                .id(id)
                .build();
    }

    @Test
    public void compare_equal()
            throws InvalidProjectNameException {
        Project lhs = createProject(1L);
        Project rhs = createProject(1L);

        assertTrue(0 == comparator.compare(lhs, rhs));
    }

    @Test
    public void compare_lessThan()
            throws InvalidProjectNameException {
        Project lhs = createProject(1L);
        Project rhs = createProject(2L);

        assertTrue(0 > comparator.compare(lhs, rhs));
    }

    @Test
    public void compare_greaterThan()
            throws InvalidProjectNameException {
        Project lhs = createProject(2L);
        Project rhs = createProject(1L);

        assertTrue(0 < comparator.compare(lhs, rhs));
    }
}
