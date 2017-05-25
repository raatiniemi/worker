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

package me.raatiniemi.worker.data.repository;

import android.content.ContentResolver;
import android.database.Cursor;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.data.provider.ProviderContract.ProjectContract;
import me.raatiniemi.worker.data.provider.ProviderContract.TimeColumns;
import me.raatiniemi.worker.data.provider.ProviderContract.TimeContract;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimeResolverRepositoryTest extends RobolectricTestCase {
    private ContentResolver contentResolver;
    private TimeResolverRepository repository;
    private Project project;

    private static Cursor buildCursorWithNumberOfItems(int numberOfItems) {
        return CursorFactory.build(
                TimeContract.getColumns(),
                numberOfItems,
                number -> Arrays.asList(number, 1L, 123456789L, 123456789L, 0L)
        );
    }

    @Before
    public void setUp() throws Exception {
        contentResolver = mock(ContentResolver.class);
        repository = new TimeResolverRepository(contentResolver);

        project = Project.builder("Name")
                .id(1L)
                .build();
    }

    @Test
    public void findProjectTimeSinceStartingPointInMilliseconds_withNullCursor() throws DomainException {
        when(
                contentResolver.query(
                        ProjectContract.getItemTimeUri(1),
                        TimeContract.getColumns(),
                        TimeColumns.START + ">=?",
                        new String[]{"1234567890"},
                        null
                )
        ).thenReturn(null);

        List<Time> time = repository.findProjectTimeSinceStartingPointInMilliseconds(project, 1234567890);

        assertTrue(time.isEmpty());
    }

    @Test
    public void findProjectTimeSinceStartingPointInMilliseconds_withEmptyCursor() throws DomainException {
        Cursor cursor = CursorFactory.buildEmpty();
        when(
                contentResolver.query(
                        ProjectContract.getItemTimeUri(1),
                        TimeContract.getColumns(),
                        TimeColumns.START + ">=?",
                        new String[]{"1234567890"},
                        null
                )
        ).thenReturn(cursor);

        List<Time> time = repository.findProjectTimeSinceStartingPointInMilliseconds(project, 1234567890);

        assertTrue(time.isEmpty());
        verify(cursor).close();
    }

    @Test
    public void findProjectTimeSinceStartingPointInMilliseconds_withRow() throws DomainException {
        Cursor cursor = buildCursorWithNumberOfItems(1);
        when(
                contentResolver.query(
                        ProjectContract.getItemTimeUri(1),
                        TimeContract.getColumns(),
                        TimeColumns.START + ">=?",
                        new String[]{"1234567890"},
                        null
                )
        ).thenReturn(cursor);

        List<Time> time = repository.findProjectTimeSinceStartingPointInMilliseconds(project, 1234567890);

        assertTrue(1 == time.size());
        verify(cursor).close();
    }

    @Test
    public void findProjectTimeSinceStartingPointInMilliseconds_withRows() throws DomainException {
        Cursor cursor = buildCursorWithNumberOfItems(5);
        when(
                contentResolver.query(
                        ProjectContract.getItemTimeUri(1),
                        TimeContract.getColumns(),
                        TimeColumns.START + ">=?",
                        new String[]{"1234567890"},
                        null
                )
        ).thenReturn(cursor);

        List<Time> time = repository.findProjectTimeSinceStartingPointInMilliseconds(project, 1234567890);

        assertTrue(5 == time.size());
        verify(cursor).close();
    }
}
