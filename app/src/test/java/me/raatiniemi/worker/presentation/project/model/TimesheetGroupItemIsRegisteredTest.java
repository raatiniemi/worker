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

package me.raatiniemi.worker.presentation.project.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetGroupItemIsRegisteredTest {
    private static final Time NOT_REGISTERED_TIME = TimeFactory.builder()
            .build();
    private final static Time REGISTERED_TIME = TimeFactory.builder()
            .register()
            .build();

    private final boolean expected;
    private final TimesheetGroupItem item;

    public TimesheetGroupItemIsRegisteredTest(
            boolean expected,
            Time... times
    ) {
        this.expected = expected;
        item = TimesheetGroupItem.build(new Date(), Arrays.asList(times));
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                Boolean.TRUE,
                                new Time[]{
                                        REGISTERED_TIME
                                }
                        },
                        {
                                Boolean.FALSE,
                                new Time[]{
                                        NOT_REGISTERED_TIME
                                }
                        },
                        {
                                Boolean.FALSE,
                                new Time[]{
                                        NOT_REGISTERED_TIME,
                                        REGISTERED_TIME
                                }
                        },
                        {
                                Boolean.TRUE,
                                new Time[]{
                                        REGISTERED_TIME,
                                        REGISTERED_TIME
                                }
                        }
                }
        );
    }

    @Test
    public void isRegistered() {
        assertEquals(expected, item.isRegistered());
    }
}
