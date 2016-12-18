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

package me.raatiniemi.worker.presentation;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.raatiniemi.worker.presentation.util.ConfirmClockOutPreferences;
import me.raatiniemi.worker.presentation.util.HideRegisteredTimePreferences;
import me.raatiniemi.worker.presentation.util.OngoingNotificationPreferences;
import me.raatiniemi.worker.presentation.util.Settings;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;

@Module
public class PreferenceModule {
    private final Settings settings;

    public PreferenceModule(SharedPreferences preferences) {
        settings = new Settings(preferences);
    }

    @Provides
    @Singleton
    HideRegisteredTimePreferences providesHideRegisteredTimePreferences() {
        return settings;
    }

    @Provides
    @Singleton
    ConfirmClockOutPreferences providesConfirmClockOutPreferences() {
        return settings;
    }

    @Provides
    @Singleton
    OngoingNotificationPreferences providesOngoingNotificationPreferences() {
        return settings;
    }

    @Provides
    @Singleton
    TimeSummaryPreferences providesTimeSummaryPreferences() {
        return settings;
    }
}
