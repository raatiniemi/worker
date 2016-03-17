/*
 * Copyright (C) 2015-2016 Worker Project
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

import android.database.Cursor;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.raatiniemi.worker.data.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class ProjectCursorMapperTest {
    private static Cursor createCursor(long id, String name, String description, long archived) {
        Cursor cursor = mock(Cursor.class);

        when(cursor.getColumnIndexOrThrow(ProjectColumns._ID)).thenReturn(0);
        when(cursor.getColumnIndexOrThrow(ProjectColumns.NAME)).thenReturn(1);
        when(cursor.getColumnIndexOrThrow(ProjectColumns.DESCRIPTION)).thenReturn(2);
        when(cursor.getColumnIndexOrThrow(ProjectColumns.ARCHIVED)).thenReturn(3);

        when(cursor.getLong(0)).thenReturn(id);
        when(cursor.getString(1)).thenReturn(name);
        when(cursor.getString(2)).thenReturn(description);
        when(cursor.getLong(3)).thenReturn(archived);

        return cursor;
    }

    private static Project createProject(long id, String name, String description, boolean archived) throws InvalidProjectNameException {
        Project project = new Project(id, name, description);
        if (archived) {
            project.archive();
        }

        return project;
    }

    @DataProvider
    public static Object[][] transform_dataProvider() throws InvalidProjectNameException {
        return new Object[][]{
                {createCursor(1, "Name", "Description", 0), createProject(1, "Name", "Description", false)},
                {createCursor(1, "Name", "Description", 1), createProject(1, "Name", "Description", true)}
        };
    }

    @Test
    @UseDataProvider("transform_dataProvider")
    public void transform(Cursor cursor, Project expected) throws InvalidProjectNameException {
        ProjectCursorMapper entityMapper = new ProjectCursorMapper();
        Project entity = entityMapper.transform(cursor);

        assertEquals(expected.getId(), entity.getId());
        assertEquals(expected.getName(), entity.getName());
        assertEquals(expected.getDescription(), entity.getDescription());
        assertEquals(expected.isArchived(), entity.isArchived());
    }
}
