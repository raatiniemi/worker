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

package me.raatiniemi.worker.data.repository;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.data.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.query.Criteria;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectResolverRepositoryTest {
    /**
     * Mapper for transforming {@link Cursor} to {@link Project}.
     */
    private final ProjectCursorMapper mCursorMapper = new ProjectCursorMapper();

    /**
     * Mapper for transforming {@link Project} to {@link ContentValues}.
     */
    private final ProjectContentValuesMapper mContentValuesMapper = new ProjectContentValuesMapper();

    /**
     * Create cursor for test data.
     *
     * @return Cursor.
     */
    private MatrixCursor createCursor() {
        return new MatrixCursor(ProjectContract.COLUMNS);
    }

    /**
     * Create a row for the cursor.
     * <p/>
     * Used for building test data.
     *
     * @param id          Id for the project.
     * @param name        Name of the project.
     * @param description Description for the project.
     * @param archived    Archive flag for the project.
     * @return Cursor with sample data.
     */
    private List<Object> createCursorRow(
            Long id,
            String name,
            String description,
            Long archived
    ) {
        List<Object> columns = new ArrayList<>();
        columns.add(id);
        columns.add(name);
        columns.add(description);
        columns.add(archived);

        return columns;
    }

    @Test
    public void matching_withNullCursor() throws InvalidProjectNameException {
        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        "name=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(null);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        Criteria criteria = Criteria.equalTo("name", "Name");
        List<Project> projects = repository.matching(criteria);
        assertTrue(projects.isEmpty());
    }

    @Test
    public void matching_withEmptyCursor() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();

        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        "name=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        Criteria criteria = Criteria.equalTo("name", "Name");
        List<Project> projects = repository.matching(criteria);
        assertTrue(projects.isEmpty());
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void matching_withRow() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();
        cursor.addRow(createCursorRow(1L, "Name", "Description", 0L));

        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        "name=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        Criteria criteria = Criteria.equalTo("name", "Name");
        List<Project> projects = repository.matching(criteria);
        assertTrue(1 == projects.size());
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void matching_withRows() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();
        cursor.addRow(createCursorRow(1L, "Name", "Description", 0L));
        cursor.addRow(createCursorRow(2L, "Name", "Description", 0L));
        cursor.addRow(createCursorRow(3L, "Name", "Description", 0L));
        cursor.addRow(createCursorRow(4L, "Name", "Description", 0L));
        cursor.addRow(createCursorRow(5L, "Name", "Description", 0L));

        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        "name=? COLLATE NOCASE",
                        new String[]{"Name"},
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        Criteria criteria = Criteria.equalTo("name", "Name");
        List<Project> projects = repository.matching(criteria);
        assertTrue(5 == projects.size());
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void get_projectsWithNullCursor() throws InvalidProjectNameException {
        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                )
        ).thenReturn(null);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        List<Project> projects = repository.get();
        assertTrue(projects.isEmpty());
    }

    @Test
    public void get_projectsWithEmptyCursor() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();

        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        List<Project> projects = repository.get();
        assertTrue(projects.isEmpty());
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void get_projectsWithRow() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();
        cursor.addRow(createCursorRow(1L, "Name", "Description", 0L));

        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        List<Project> projects = repository.get();
        assertTrue(1 == projects.size());
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void get_projectsWithRows() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();
        cursor.addRow(createCursorRow(1L, "Name", "Description", 0L));
        cursor.addRow(createCursorRow(2L, "Name", "Description", 0L));
        cursor.addRow(createCursorRow(3L, "Name", "Description", 0L));
        cursor.addRow(createCursorRow(4L, "Name", "Description", 0L));
        cursor.addRow(createCursorRow(5L, "Name", "Description", 0L));

        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getStreamUri(),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        List<Project> projects = repository.get();
        assertTrue(5 == projects.size());
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void get_projectWithNullCursor() throws InvalidProjectNameException {
        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getItemUri(1),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                )
        ).thenReturn(null);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        assertNull(repository.get(1));
    }

    @Test
    public void get_projectWithoutRow() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();

        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getItemUri(1),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        assertNull(repository.get(1));
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void get_projectWithRow() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();
        cursor.addRow(createCursorRow(1L, "Name", "Description", 0L));

        ContentResolver resolver = mock(ContentResolver.class);
        when(
                resolver.query(
                        ProjectContract.getItemUri(1),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        assertNotNull(repository.get(1));
        assertTrue("Failed to close cursor", cursor.isClosed());
    }

    @Test
    public void add() throws InvalidProjectNameException {
        MatrixCursor cursor = createCursor();
        cursor.addRow(createCursorRow(1L, "Name", "Description", 0L));

        Project project = new Project("Name");
        project.setDescription("Description");

        ContentResolver resolver = mock(ContentResolver.class);

        // insert...
        when(
                resolver.insert(
                        ProjectContract.getStreamUri(),
                        mContentValuesMapper.transform(project)
                )
        ).thenReturn(ProjectContract.getItemUri(1));

        // get...
        when(
                resolver.query(
                        ProjectContract.getItemUri(1),
                        ProjectContract.COLUMNS,
                        null,
                        null,
                        null
                )
        ).thenReturn(cursor);

        ProjectResolverRepository repository = new ProjectResolverRepository(
                resolver,
                mCursorMapper,
                mContentValuesMapper
        );

        project = repository.add(project);
        assertNotNull(project);
        assertEquals(Long.valueOf(1L), project.getId());
    }
}
