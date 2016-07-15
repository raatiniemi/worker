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
import android.database.Cursor;
import android.database.MatrixCursor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.data.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.data.WorkerContract.TimeContract;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.query.Criteria;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TimeResolverRepositoryTest {
    private ContentResolver mContentResolver;
    private TimeResolverRepository mRepository;
    private Project mProject;

    @Before
    public void setUp() throws Exception {
        mContentResolver = mock(ContentResolver.class);
        mRepository = new TimeResolverRepository(
                mContentResolver,
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );

        mProject = new Project.Builder("Name")
                .id(1L)
                .build();
    }

    private Cursor buildCursorWithNumberOfItems(int numberOfItems) {
        MatrixCursor cursor = buildCursor();

        for (long i = 0; i < numberOfItems; i++) {
            cursor.addRow(buildCursorRow());
        }

        return cursor;
    }

    private MatrixCursor buildCursor() {
        return spy(new MatrixCursor(TimeContract.getColumns()));
    }

    private List<Object> buildCursorRow() {
        List<Object> values = new ArrayList<>();
        values.add(1L);
        values.add(1L);
        values.add(123456789L);
        values.add(123456789L);
        values.add(0L);

        return values;
    }

    @Test
    public void matching_withNullCursor() throws DomainException {
        when(
                mContentResolver.query(
                        ProjectContract.getItemTimeUri(1),
                        TimeContract.getColumns(),
                        "start>=? COLLATE NOCASE",
                        new String[]{"1234567890"},
                        null
                )
        ).thenReturn(null);

        Criteria criteria = Criteria.moreThanOrEqualTo(TimeColumns.START, 1234567890);
        List<Time> time = mRepository.matching(mProject, criteria);

        assertTrue(time.isEmpty());
    }

    @Test
    public void matching_withEmptyCursor() throws DomainException {
        Cursor cursor = buildCursorWithNumberOfItems(0);
        when(
                mContentResolver.query(
                        ProjectContract.getItemTimeUri(1),
                        TimeContract.getColumns(),
                        "start>=? COLLATE NOCASE",
                        new String[]{"1234567890"},
                        null
                )
        ).thenReturn(cursor);

        Criteria criteria = Criteria.moreThanOrEqualTo(TimeColumns.START, 1234567890);
        List<Time> time = mRepository.matching(mProject, criteria);

        assertTrue(time.isEmpty());
        verify(cursor).close();
    }

    @Test
    public void matching_withRow() throws DomainException {
        Cursor cursor = buildCursorWithNumberOfItems(1);
        when(
                mContentResolver.query(
                        ProjectContract.getItemTimeUri(1),
                        TimeContract.getColumns(),
                        "start>=? COLLATE NOCASE",
                        new String[]{"1234567890"},
                        null
                )
        ).thenReturn(cursor);

        Criteria criteria = Criteria.moreThanOrEqualTo(TimeColumns.START, 1234567890);
        List<Time> time = mRepository.matching(mProject, criteria);

        assertTrue(1 == time.size());
        verify(cursor).close();
    }

    @Test
    public void matching_withRows() throws DomainException {
        Cursor cursor = buildCursorWithNumberOfItems(5);
        when(
                mContentResolver.query(
                        ProjectContract.getItemTimeUri(1),
                        TimeContract.getColumns(),
                        "start>=? COLLATE NOCASE",
                        new String[]{"1234567890"},
                        null
                )
        ).thenReturn(cursor);

        Criteria criteria = Criteria.moreThanOrEqualTo(TimeColumns.START, 1234567890);
        List<Time> time = mRepository.matching(mProject, criteria);

        assertTrue(5 == time.size());
        verify(cursor).close();
    }
}
