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
import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.data.provider.ProviderContract.ProjectColumns;
import me.raatiniemi.worker.data.repository.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectResolverRepositoryTest extends RobolectricTestCase {
    private final ProjectContentValuesMapper contentValuesMapper = new ProjectContentValuesMapper();
    private ContentResolver contentResolver;
    private ProjectResolverRepository repository;

    private static Cursor buildCursorWithNumberOfItems(int numberOfItems) {
        return CursorFactory.build(
                ProviderContract.Project.getColumns(),
                numberOfItems,
                number -> Arrays.asList(number, "Name")
        );
    }

    @Before
    public void setUp() {
        contentResolver = mock(ContentResolver.class);
        repository = new ProjectResolverRepository(contentResolver);
    }

    @Test
    public void findProjectByName_withNullCursor() throws InvalidProjectNameException {
        when(
                contentResolver.query(
                        ProviderContract.Project.getStreamUri(),
                        ProviderContract.Project.getColumns(),
                        ProjectColumns.NAME + "=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(null);

        Optional<Project> value = repository.findProjectByName("Name");

        assertFalse(value.isPresent());
    }

    @Test
    public void findProjectByName_withEmptyCursor() throws InvalidProjectNameException {
        Cursor cursor = CursorFactory.buildEmpty();
        when(
                contentResolver.query(
                        ProviderContract.Project.getStreamUri(),
                        ProviderContract.Project.getColumns(),
                        ProjectColumns.NAME + "=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        Optional<Project> value = repository.findProjectByName("Name");

        assertFalse(value.isPresent());
        verify(cursor).close();
    }

    @Test
    public void findProjectByName_withProject() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(1);
        when(
                contentResolver.query(
                        ProviderContract.Project.getStreamUri(),
                        ProviderContract.Project.getColumns(),
                        ProjectColumns.NAME + "=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        Optional<Project> value = repository.findProjectByName("Name");

        assertTrue(value.isPresent());
        verify(cursor).close();
    }

    @Test
    public void get_projectsWithNullCursor() throws InvalidProjectNameException {
        when(
                contentResolver.query(
                        ProviderContract.Project.getStreamUri(),
                        ProviderContract.Project.getColumns(),
                        null,
                        null,
                        ProviderContract.Project.ORDER_BY
                )
        ).thenReturn(null);

        List<Project> projects = repository.get();

        assertTrue(projects.isEmpty());
    }

    @Test
    public void get_projectsWithEmptyCursor() throws InvalidProjectNameException {
        Cursor cursor = CursorFactory.buildEmpty();
        when(
                contentResolver.query(
                        ProviderContract.Project.getStreamUri(),
                        ProviderContract.Project.getColumns(),
                        null,
                        null,
                        ProviderContract.Project.ORDER_BY
                )
        ).thenReturn(cursor);

        List<Project> projects = repository.get();

        assertTrue(projects.isEmpty());
        verify(cursor).close();
    }

    @Test
    public void get_projectsWithRow() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(1);
        when(
                contentResolver.query(
                        ProviderContract.Project.getStreamUri(),
                        ProviderContract.Project.getColumns(),
                        null,
                        null,
                        ProviderContract.Project.ORDER_BY
                )
        ).thenReturn(cursor);

        List<Project> projects = repository.get();

        assertTrue(1 == projects.size());
        verify(cursor).close();
    }

    @Test
    public void get_projectsWithRows() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(5);
        when(
                contentResolver.query(
                        ProviderContract.Project.getStreamUri(),
                        ProviderContract.Project.getColumns(),
                        null,
                        null,
                        ProviderContract.Project.ORDER_BY
                )
        ).thenReturn(cursor);

        List<Project> projects = repository.get();

        assertTrue(5 == projects.size());
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void get_projectWithNullCursor() throws InvalidProjectNameException {
        when(
                contentResolver.query(
                        ProviderContract.Project.getItemUri(1),
                        ProviderContract.Project.getColumns(),
                        null,
                        null,
                        null
                )
        ).thenReturn(null);

        Optional<Project> value = repository.get(1);

        assertFalse(value.isPresent());
    }

    @Test
    public void get_projectWithoutRow() throws InvalidProjectNameException {
        Cursor cursor = CursorFactory.buildEmpty();
        when(
                contentResolver.query(
                        ProviderContract.Project.getItemUri(1),
                        ProviderContract.Project.getColumns(),
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        Optional<Project> value = repository.get(1);

        assertFalse(value.isPresent());
        verify(cursor).close();
    }

    @Test
    public void get_projectWithRow() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(1);
        when(
                contentResolver.query(
                        ProviderContract.Project.getItemUri(1),
                        ProviderContract.Project.getColumns(),
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        Optional<Project> value = repository.get(1);

        assertTrue(value.isPresent());
        verify(cursor).close();
    }

    @Test
    public void add() throws InvalidProjectNameException {
        Project project = Project.builder("Name")
                .build();
        Cursor cursor = buildCursorWithNumberOfItems(1);
        // insert...
        when(
                contentResolver.insert(
                        ProviderContract.Project.getStreamUri(),
                        contentValuesMapper.transform(project)
                )
        ).thenReturn(ProviderContract.Project.getItemUri(1));
        // get...
        when(
                contentResolver.query(
                        ProviderContract.Project.getItemUri(1),
                        ProviderContract.Project.getColumns(),
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        Optional<Project> value = repository.add(project);

        assertTrue(value.isPresent());
        assertEquals(Long.valueOf(1L), value.get().getId());
        verify(cursor).close();
    }
}
