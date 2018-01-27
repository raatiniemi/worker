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

package me.raatiniemi.worker.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ProjectIsActiveTest {
    private final String message;
    private final boolean expected;
    private final List<Time> times;

    public ProjectIsActiveTest(String message, boolean expected, List<Time> times) {
        this.message = message;
        this.expected = expected;
        this.times = times;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Without items",
                                Boolean.FALSE,
                                Collections.emptyList()
                        },
                        {
                                "Without active item",
                                Boolean.FALSE,
                                Collections.singletonList(
                                        TimeFactory.builder()
                                                .stopInMilliseconds(1L)
                                                .build()
                                )
                        },
                        {
                                "With active item",
                                Boolean.TRUE,
                                Collections.singletonList(
                                        TimeFactory.builder()
                                                .stopInMilliseconds(0L)
                                                .build()
                                )
                        }
                }
        );
    }

    @Test
    public void isActive() throws InvalidProjectNameException {
        Project project = Project.builder("Project name")
                .build();
        project.addTime(times);

        assertTrue(message, expected == project.isActive());
    }
}
