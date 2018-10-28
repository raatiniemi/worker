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

package me.raatiniemi.worker.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import me.raatiniemi.worker.factory.TimeIntervalFactory;
import me.raatiniemi.worker.util.Optional;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class IsProjectActiveTest {
    private TimeIntervalRepository timeIntervalRepository;

    @Before
    public void setUp() throws Exception {
        timeIntervalRepository = mock(TimeIntervalRepository.class);
    }

    @Test
    public void execute_withoutTime() throws DomainException {
        when(timeIntervalRepository.findActiveByProjectId(1L))
                .thenReturn(Optional.empty());

        IsProjectActive isProjectActive = new IsProjectActive(timeIntervalRepository);
        assertFalse(isProjectActive.execute(1L));
    }

    @Test
    public void execute_withActiveTime() throws DomainException {
        TimeInterval timeInterval = TimeIntervalFactory.builder()
                .stopInMilliseconds(0L)
                .build();
        when(timeIntervalRepository.findActiveByProjectId(1L))
                .thenReturn(Optional.of(timeInterval));

        IsProjectActive isProjectActive = new IsProjectActive(timeIntervalRepository);
        assertTrue(isProjectActive.execute(1L));
    }
}
