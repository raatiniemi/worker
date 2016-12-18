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

package me.raatiniemi.worker.presentation.project;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.interactor.GetTimesheet;
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime;
import me.raatiniemi.worker.domain.interactor.RemoveTime;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.project.presenter.TimesheetPresenter;
import me.raatiniemi.worker.presentation.util.HideRegisteredTimePreferences;

@Module
public class ProjectModule {
    @Provides
    @Singleton
    TimesheetPresenter providesTimesheetPresenter(
            Context context,
            HideRegisteredTimePreferences hideRegisteredTimePreferences
    ) {
        TimeRepository timeRepository = new TimeResolverRepository(
                context.getContentResolver(),
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );

        return new TimesheetPresenter(
                context,
                hideRegisteredTimePreferences,
                EventBus.getDefault(),
                new GetTimesheet(timeRepository),
                new MarkRegisteredTime(timeRepository),
                new RemoveTime(timeRepository)
        );
    }
}
