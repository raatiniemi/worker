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

package me.raatiniemi.worker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.leakcanary.LeakCanary;

import me.raatiniemi.worker.data.DataModule;
import me.raatiniemi.worker.data.service.ongoing.DaggerOngoingServiceComponent;
import me.raatiniemi.worker.data.service.ongoing.OngoingServiceComponent;
import me.raatiniemi.worker.data.service.ongoing.ReloadNotificationService;
import me.raatiniemi.worker.presentation.AndroidModule;
import me.raatiniemi.worker.presentation.PreferenceModule;
import me.raatiniemi.worker.presentation.project.DaggerProjectComponent;
import me.raatiniemi.worker.presentation.project.ProjectComponent;
import me.raatiniemi.worker.presentation.project.ProjectModule;
import me.raatiniemi.worker.presentation.projects.DaggerProjectsComponent;
import me.raatiniemi.worker.presentation.projects.ProjectsComponent;
import me.raatiniemi.worker.presentation.projects.ProjectsModule;
import me.raatiniemi.worker.presentation.settings.DaggerSettingsComponent;
import me.raatiniemi.worker.presentation.settings.SettingsComponent;
import me.raatiniemi.worker.presentation.settings.SettingsModule;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Stores application constants.
 */
public class WorkerApplication extends Application {
    /**
     * Package for the application.
     */
    public static final String PACKAGE = "me.raatiniemi.worker";

    /**
     * Name of the application database.
     */
    public static final String DATABASE_NAME = "worker";

    public static final int NOTIFICATION_BACKUP_SERVICE_ID = 1;
    public static final int NOTIFICATION_RESTORE_SERVICE_ID = 2;

    /**
     * Id for on-going notification.
     */
    public static final int NOTIFICATION_ON_GOING_ID = 3;

    /**
     * Prefix for backup directories.
     */
    public static final String STORAGE_BACKUP_DIRECTORY_PREFIX = "backup-";

    /**
     * Pattern for the backup directories.
     */
    public static final String STORAGE_BACKUP_DIRECTORY_PATTERN
            = WorkerApplication.STORAGE_BACKUP_DIRECTORY_PREFIX + "(\\d+)";

    /**
     * Intent action for restarting the application.
     */
    public static final String INTENT_ACTION_RESTART = "action_restart";

    private OngoingServiceComponent ongoingServiceComponent;
    private ProjectComponent projectComponent;
    private ProjectsComponent projectsComponent;
    private SettingsComponent settingsComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidModule androidModule = new AndroidModule(this);
        DataModule dataModule = new DataModule(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceModule preferenceModule = new PreferenceModule(preferences);
        ongoingServiceComponent = DaggerOngoingServiceComponent.builder()
                .dataModule(dataModule)
                .preferenceModule(preferenceModule)
                .build();
        projectComponent = DaggerProjectComponent.builder()
                .dataModule(dataModule)
                .preferenceModule(preferenceModule)
                .projectModule(new ProjectModule())
                .build();
        projectsComponent = DaggerProjectsComponent.builder()
                .androidModule(androidModule)
                .dataModule(dataModule)
                .projectsModule(new ProjectsModule())
                .preferenceModule(preferenceModule)
                .build();
        settingsComponent = DaggerSettingsComponent.builder()
                .preferenceModule(preferenceModule)
                .settingsModule(new SettingsModule())
                .build();

        if (!isUnitTesting()) {
            LeakCanary.install(this);
            ReloadNotificationService.startServiceWithContext(this);
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        }
    }

    public OngoingServiceComponent getOngoingServiceComponent() {
        return ongoingServiceComponent;
    }

    public ProjectComponent getProjectComponent() {
        return projectComponent;
    }

    public ProjectsComponent getProjectsComponent() {
        return projectsComponent;
    }

    public SettingsComponent getSettingsComponent() {
        return settingsComponent;
    }

    boolean isUnitTesting() {
        return false;
    }
}
