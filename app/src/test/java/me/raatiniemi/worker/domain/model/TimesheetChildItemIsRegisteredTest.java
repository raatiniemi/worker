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

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.model.TimesheetChildItem;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TimesheetChildItemIsRegisteredTest {
    private final String message;
    private final boolean expected;
    private final Time time;

    public TimesheetChildItemIsRegisteredTest(
            String message,
            boolean expected,
            Time time
    ) {
        this.message = message;
        this.expected = expected;
        this.time = time;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "is registered",
                                Boolean.TRUE,
                                TimeFactory.builder()
                                        .register()
                                        .build()
                        },
                        {
                                "is not registered",
                                Boolean.FALSE,
                                TimeFactory.builder()
                                        .build()
                        }
                }
        );
    }

    @Test
    public void isRegistered() {
        TimesheetChildItem childItem = new TimesheetChildItem(time);

        assertTrue(message, expected == childItem.isRegistered());
    }
}
