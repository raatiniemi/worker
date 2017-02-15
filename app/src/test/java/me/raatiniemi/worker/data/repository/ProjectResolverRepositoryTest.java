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
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.provider.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.data.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectResolverRepositoryTest extends RobolectricTestCase {
    private final ProjectCursorMapper cursorMapper = new ProjectCursorMapper();
    private final ProjectContentValuesMapper contentValuesMapper = new ProjectContentValuesMapper();
    private ContentResolver contentResolver;
    private ProjectResolverRepository repository;

    private static Cursor buildCursorWithNumberOfItems(int numberOfItems) {
        return CursorFactory.build(
                ProjectContract.getColumns(),
                numberOfItems,
                number -> Arrays.asList(number, "Name")
        );
    }

    @Before
    public void setUp() {
        contentResolver = mock(ContentResolver.class);
        repository = new ProjectResolverRepository(
                contentResolver,
                cursorMapper,
                contentValuesMapper
        );
    }

    @Test
    public void findProjectByName_withNullCursor() throws InvalidProjectNameException {
        when(
                contentResolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        ProjectColumns.NAME + "=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(null);

        Project project = repository.findProjectByName("Name");

        assertNull(project);
    }

    @Test
    public void findProjectByName_withEmptyCursor() throws InvalidProjectNameException {
        Cursor cursor = CursorFactory.buildEmpty();
        when(
                contentResolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        ProjectColumns.NAME + "=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        Project project = repository.findProjectByName("Name");

        assertNull(project);
        verify(cursor).close();
    }

    @Test
    public void findProjectByName_withProject() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(1);
        when(
                contentResolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        ProjectColumns.NAME + "=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        Project project = repository.findProjectByName("Name");

        assertNotNull(project);
        verify(cursor).close();
    }

    @Test
    public void get_projectsWithNullCursor() throws InvalidProjectNameException {
        when(
                contentResolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        null,
                        null,
                        ProjectContract.ORDER_BY
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
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        null,
                        null,
                        ProjectContract.ORDER_BY
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
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        null,
                        null,
                        ProjectContract.ORDER_BY
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
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        null,
                        null,
                        ProjectContract.ORDER_BY
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
                        ProjectContract.getItemUri(1),
                        ProjectContract.getColumns(),
                        null,
                        null,
                        null
                )
        ).thenReturn(null);

        Project project = repository.get(1);

        assertNull(project);
    }

    @Test
    public void get_projectWithoutRow() throws InvalidProjectNameException {
        Cursor cursor = CursorFactory.buildEmpty();
        when(
                contentResolver.query(
                        ProjectContract.getItemUri(1),
                        ProjectContract.getColumns(),
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        Project project = repository.get(1);

        assertNull(project);
        verify(cursor).close();
    }

    @Test
    public void get_projectWithRow() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(1);
        when(
                contentResolver.query(
                        ProjectContract.getItemUri(1),
                        ProjectContract.getColumns(),
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        Project project = repository.get(1);

        assertNotNull(project);
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
                        ProjectContract.getStreamUri(),
                        contentValuesMapper.transform(project)
                )
        ).thenReturn(ProjectContract.getItemUri(1));
        // get...
        when(
                contentResolver.query(
                        ProjectContract.getItemUri(1),
                        ProjectContract.getColumns(),
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        project = repository.add(project);

        assertNotNull(project);
        assertEquals(Long.valueOf(1L), project.getId());
        verify(cursor).close();
    }
}
