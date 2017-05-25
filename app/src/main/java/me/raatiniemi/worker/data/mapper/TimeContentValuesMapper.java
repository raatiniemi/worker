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

package me.raatiniemi.worker.data.mapper;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.provider.ProviderContract.TimeColumns;
import me.raatiniemi.worker.domain.model.Time;

/**
 * Handle transformation from {@link Time} to {@link ContentValues}.
 */
public class TimeContentValuesMapper implements ContentValuesMapper<Time> {
    @NonNull
    @Override
    public ContentValues transform(@NonNull Time entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimeColumns.PROJECT_ID, entity.getProjectId());
        contentValues.put(TimeColumns.START, entity.getStartInMilliseconds());
        contentValues.put(TimeColumns.STOP, entity.getStopInMilliseconds());
        contentValues.put(TimeColumns.REGISTERED, entity.isRegistered() ? 1L : 0L);

        return contentValues;
    }
}
