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
import android.database.MatrixCursor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.data.provider.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.query.Criteria;
import me.raatiniemi.worker.RobolectricTestCase;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectResolverRepositoryTest extends RobolectricTestCase {
    private final ProjectCursorMapper cursorMapper = new ProjectCursorMapper();
    private final ProjectContentValuesMapper contentValuesMapper = new ProjectContentValuesMapper();
    private ContentResolver contentResolver;
    private ProjectResolverRepository repository;

    @Before
    public void setUp() {
        contentResolver = mock(ContentResolver.class);
        repository = new ProjectResolverRepository(
                contentResolver,
                cursorMapper,
                contentValuesMapper
        );
    }

    private Cursor buildCursorWithNumberOfItems(int numberOfItems) {
        MatrixCursor cursor = buildCursor();

        for (long i = 1; i <= numberOfItems; i++) {
            cursor.addRow(buildCursorRow(i, "Name"));
        }

        return cursor;
    }

    private MatrixCursor buildCursor() {
        return spy(new MatrixCursor(ProjectContract.getColumns()));
    }

    private List<Object> buildCursorRow(Long id, String name) {
        List<Object> columns = new ArrayList<>();
        columns.add(id);
        columns.add(name);

        return columns;
    }

    @Test
    public void matching_withNullCursor() throws InvalidProjectNameException {
        when(
                contentResolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        "name=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(null);

        Criteria criteria = Criteria.equalTo("name", "Name");
        List<Project> projects = repository.matching(criteria);

        assertTrue(projects.isEmpty());
    }

    @Test
    public void matching_withEmptyCursor() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(0);
        when(
                contentResolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        "name=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        Criteria criteria = Criteria.equalTo("name", "Name");
        List<Project> projects = repository.matching(criteria);

        assertTrue(projects.isEmpty());
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void matching_withRow() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(1);
        when(
                contentResolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        "name=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        Criteria criteria = Criteria.equalTo("name", "Name");
        List<Project> projects = repository.matching(criteria);

        assertTrue(1 == projects.size());
        verify(cursor).close();
    }

    @Test
    public void matching_withRows() throws InvalidProjectNameException {
        Cursor cursor = buildCursorWithNumberOfItems(5);
        when(
                contentResolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.getColumns(),
                        "name=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        Criteria criteria = Criteria.equalTo("name", "Name");
        List<Project> projects = repository.matching(criteria);

        assertTrue(5 == projects.size());
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
        Cursor cursor = buildCursorWithNumberOfItems(0);
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
        Cursor cursor = buildCursorWithNumberOfItems(0);
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
