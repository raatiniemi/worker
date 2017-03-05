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

    private boolean expected;
    private TimesheetChildItem[] childItems;

    public TimesheetGroupItemIsRegisteredTest(
            boolean expected,
            TimesheetChildItem... childItems
    ) {
        this.expected = expected;
        this.childItems = childItems;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                Boolean.TRUE,
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(REGISTERED_TIME)
                                }
                        },
                        {
                                Boolean.FALSE,
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(NOT_REGISTERED_TIME)
                                }
                        },
                        {
                                Boolean.FALSE,
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(NOT_REGISTERED_TIME),
                                        new TimesheetChildItem(REGISTERED_TIME)
                                }
                        },
                        {
                                Boolean.TRUE,
                                new TimesheetChildItem[]{
                                        new TimesheetChildItem(REGISTERED_TIME),
                                        new TimesheetChildItem(REGISTERED_TIME)
                                }
                        }
                }
        );
    }

    @Test
    public void isRegistered() {
        TimesheetGroupItem groupItem = new TimesheetGroupItem(new Date());
        for (TimesheetChildItem childItem : childItems) {
            groupItem.add(childItem);
        }

        assertEquals(expected, groupItem.isRegistered());
    }
}
