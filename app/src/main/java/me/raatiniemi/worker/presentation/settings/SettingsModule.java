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

package me.raatiniemi.worker.presentation.settings;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.raatiniemi.worker.presentation.settings.presenter.ProjectPresenter;
import me.raatiniemi.worker.presentation.settings.presenter.SettingsPresenter;

@Module
public class SettingsModule {
    @Provides
    @Singleton
    SettingsPresenter providesSettingsPresenter(Context context) {
        return new SettingsPresenter(context, EventBus.getDefault());
    }

    @Provides
    @Singleton
    ProjectPresenter providesProjectPresenter(Context context) {
        return new ProjectPresenter(context, EventBus.getDefault());
    }
}
