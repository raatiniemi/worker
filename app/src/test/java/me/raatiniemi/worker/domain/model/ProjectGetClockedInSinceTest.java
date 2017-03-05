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
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static me.raatiniemi.worker.util.NullUtil.isNull;

@RunWith(Parameterized.class)
public class ProjectGetClockedInSinceTest {
    private final String message;
    private final Date expected;
    private final List<Time> times;

    public ProjectGetClockedInSinceTest(String message, Date expected, List<Time> times) {
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
                                null,
                                Collections.emptyList()
                        },
                        {
                                "Without active item",
                                null,
                                Collections.singletonList(
                                        TimeFactory.builder()
                                                .stopInMilliseconds(1L)
                                                .build())
                        },
                        {
                                "With active item",
                                new Date(50000L),
                                Collections.singletonList(
                                        TimeFactory.builder()
                                                .startInMilliseconds(50000L)
                                                .build()
                                )
                        }
                }
        );
    }

    @Test
    public void getClockedInSince() throws InvalidProjectNameException {
        Project project = Project.builder("Project name")
                .build();
        project.addTime(times);

        if (isNull(expected)) {
            assertNull(message, project.getClockedInSince());
            return;
        }
        assertEquals(message, expected, project.getClockedInSince());
    }
}
