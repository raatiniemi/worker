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

package me.raatiniemi.worker.presentation.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.domain.repository.TimeRepository;

public class ClockOutService extends IntentService {
    private static final String TAG = "ClockOutService";

    public ClockOutService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ClockOut clockOut = new ClockOut(getTimeRepository());
            clockOut.execute(getProjectId(intent), new Date());
        } catch (Exception e) {
            Log.w(TAG, "Unable to clock out project: " + e.getMessage());
        }
    }

    private TimeRepository getTimeRepository() {
        return new TimeResolverRepository(
                getContentResolver(),
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );
    }

    private long getProjectId(Intent intent) {
        String itemId = WorkerContract.ProjectContract.getItemId(intent.getData());
        long projectId = Long.valueOf(itemId);
        if (0 == projectId) {
            throw new IllegalArgumentException("Unable to extract project id from URI");
        }

        return projectId;
    }
}
