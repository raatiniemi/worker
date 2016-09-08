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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(Parameterized.class)
public class ProjectGetClockedInSinceTest {
    private String message;
    private Date expected;
    private Time[] times;

    public ProjectGetClockedInSinceTest(
            String message,
            Date expected,
            Time... times
    ) {
        this.message = message;
        this.expected = expected;
        this.times = times;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws ClockOutBeforeClockInException {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Without items",
                                null,
                                new Time[]{}
                        },
                        {
                                "Without active item",
                                null,
                                new Time[]{
                                        new Time.Builder(1L)
                                                .stopInMilliseconds(1L)
                                                .build()
                                }
                        },
                        {
                                "With active item",
                                new Date(50000L),
                                new Time[]{
                                        new Time.Builder(1L)
                                                .startInMilliseconds(50000L)
                                                .build()
                                }
                        }
                }
        );
    }

    @Test
    public void getClockedInSince() throws InvalidProjectNameException {
        Project project = new Project.Builder("Project name")
                .build();
        project.addTime(Arrays.asList(times));

        if (null == expected) {
            assertNull(message, project.getClockedInSince());
            return;
        }
        assertEquals(message, expected, project.getClockedInSince());
    }
}
