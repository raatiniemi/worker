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

package me.raatiniemi.worker.data.mapper;

import android.content.ContentValues;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.data.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectContentValuesMapperTest {
    private static ContentValues createContentValues(
            final String name,
            final String description,
            final long archived
    ) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProjectColumns.NAME, name);
        contentValues.put(ProjectColumns.DESCRIPTION, description);
        contentValues.put(ProjectColumns.ARCHIVED, archived);

        return contentValues;
    }

    private static Project createProject(
            final String name,
            final String description,
            final boolean archived
    ) throws InvalidProjectNameException {
        Project project = new Project(null, name);
        project.describe(description);
        if (archived) {
            project.archive();
        }

        return project;
    }

    @Test
    public void transform() throws InvalidProjectNameException {
        ProjectContentValuesMapper entityMapper = new ProjectContentValuesMapper();

        ContentValues expected = createContentValues("Name", "Description", 0L);
        Project project = createProject("Name", "Description", false);
        ContentValues contentValues = entityMapper.transform(project);

        // the id column should not be mapped since that would introduce the
        // possibility of the id being modified.
        assertNull(contentValues.get(ProjectColumns._ID));
        assertEquals(expected.get(ProjectColumns.NAME), contentValues.get(ProjectColumns.NAME));
        assertEquals(expected.get(ProjectColumns.DESCRIPTION), contentValues.get(ProjectColumns.DESCRIPTION));
        assertEquals(expected.get(ProjectColumns.ARCHIVED), contentValues.get(ProjectColumns.ARCHIVED));

        expected = createContentValues("Name", "Description", 1L);
        project = createProject("Name", "Description", true);
        contentValues = entityMapper.transform(project);

        // the id column should not be mapped since that would introduce the
        // possibility of the id being modified.
        assertNull(contentValues.get(ProjectColumns._ID));
        assertEquals(expected.get(ProjectColumns.NAME), contentValues.get(ProjectColumns.NAME));
        assertEquals(expected.get(ProjectColumns.DESCRIPTION), contentValues.get(ProjectColumns.DESCRIPTION));
        assertEquals(expected.get(ProjectColumns.ARCHIVED), contentValues.get(ProjectColumns.ARCHIVED));
    }
}
