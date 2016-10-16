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

package me.raatiniemi.worker;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import me.raatiniemi.worker.data.service.ongoing.ReloadNotificationService;
import me.raatiniemi.worker.presentation.AndroidModule;
import me.raatiniemi.worker.presentation.projects.DaggerProjectsComponent;
import me.raatiniemi.worker.presentation.projects.ProjectsComponent;
import me.raatiniemi.worker.presentation.projects.ProjectsModule;

/**
 * Stores application constants.
 */
public class Worker extends Application {
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
            = Worker.STORAGE_BACKUP_DIRECTORY_PREFIX + "(\\d+)";

    /**
     * Intent action for restarting the application.
     */
    public static final String INTENT_ACTION_RESTART = "action_restart";

    private ProjectsComponent projectsComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        projectsComponent = DaggerProjectsComponent.builder()
                .androidModule(new AndroidModule(this))
                .projectsModule(new ProjectsModule())
                .build();

        if (!isUnitTesting()) {
            LeakCanary.install(this);
            ReloadNotificationService.startServiceWithContext(this);
        }
    }

    public ProjectsComponent getProjectsComponent() {
        return projectsComponent;
    }

    boolean isUnitTesting() {
        return false;
    }
}
