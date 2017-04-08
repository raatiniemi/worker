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

package me.raatiniemi.worker.data;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.provider.WorkerDatabase;
import me.raatiniemi.worker.data.repository.ProjectResolverRepository;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;

@Module
public class DataModule {
    final Context context;

    public DataModule(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Provides
    @Singleton
    WorkerDatabase providesWorkerDatabase() {
        return new WorkerDatabase(context);
    }

    @NonNull
    @Provides
    @Singleton
    ProjectRepository providesProjectRepository() {
        return new ProjectResolverRepository(
                context.getContentResolver(),
                new ProjectCursorMapper(),
                new ProjectContentValuesMapper()
        );
    }

    @NonNull
    @Provides
    @Singleton
    TimeRepository providesTimeRepository() {
        return new TimeResolverRepository(
                context.getContentResolver(),
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );
    }
}
