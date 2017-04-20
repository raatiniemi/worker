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

import android.database.Cursor;
import android.database.MatrixCursor;

import java.util.List;
import java.util.Objects;

import me.raatiniemi.worker.util.Function;

import static org.mockito.Mockito.spy;

final class CursorFactory {
    private CursorFactory() {
    }

    static Cursor buildEmpty() {
        return spy(new MatrixCursor(new String[]{}, 0));
    }

    static <T> Cursor build(
            String[] columnNames,
            Integer numberOfItems,
            Function<Integer, List<T>> function
    ) {
        MatrixCursor cursor = spy(new MatrixCursor(columnNames, numberOfItems));

        for (Integer i = 1; i <= numberOfItems; i++) {
            List<T> row = function.apply(i);

            cursor.addRow(Objects.requireNonNull(row));
        }

        return cursor;
    }
}
