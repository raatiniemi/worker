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

package me.raatiniemi.worker.data.service.ongoing;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.shadows.ShadowNotificationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class ReloadNotificationServiceTest extends RobolectricTestCase {
    private final ShadowNotificationManager nm = shadowOf((NotificationManager) RuntimeEnvironment
            .application
            .getSystemService(Context.NOTIFICATION_SERVICE));

    private ServiceController<TestService> serviceController;

    private GetProjects getProjects;

    private Project buildActiveProjectWithId(Long projectId) throws DomainException {
        return buildProjectWithIdAndStatus(projectId, true);
    }

    private Project buildInactiveProjectWithId() throws DomainException {
        return buildProjectWithIdAndStatus(1L, false);
    }

    private Project buildProjectWithIdAndStatus(Long projectId, boolean isActive)
            throws DomainException {
        Project project = Project.builder("Project name")
                .id(projectId)
                .build();

        if (isActive) {
            Time time = TimeFactory.builder()
                    .stopInMilliseconds(0L)
                    .build();

            project.addTime(Collections.singletonList(time));
        }

        return project;
    }

    @Before
    public void setUp() {
        serviceController = Robolectric.buildService(TestService.class);
        serviceController.create()
                .get();

        setUpService();
    }

    private void setUpService() {
        TestService service = getService();
        service.getProjects = getProjects = mock(GetProjects.class);
    }

    private TestService getService() {
        return serviceController.get();
    }

    @After
    public void tearDown() {
        serviceController.destroy();
    }

    @Test
    public void onHandleIntent_withOngoingNotificationDisabled() throws DomainException {
        serviceController.startCommand(0, 0);

        verify(getProjects, never()).execute();
        assertNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @Test
    public void onHandleIntent_withActiveProjectsAndOngoingNotificationDisabled()
            throws DomainException {
        List<Project> projects = new ArrayList<>();
        projects.add(buildActiveProjectWithId(1L));
        when(getProjects.execute())
                .thenReturn(projects);

        serviceController.startCommand(0, 0);

        verify(getProjects, never()).execute();
        assertNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @Test
    public void onHandleIntent_withoutProjects() throws DomainException {
        getService().enableOngoingNotification();
        when(getProjects.execute())
                .thenReturn(Collections.emptyList());

        serviceController.startCommand(0, 0);

        assertNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @Test
    public void onHandleIntent_withoutActiveProjects() throws DomainException {
        getService().enableOngoingNotification();
        List<Project> projects = new ArrayList<>();
        projects.add(buildInactiveProjectWithId());
        when(getProjects.execute())
                .thenReturn(projects);

        serviceController.startCommand(0, 0);

        assertNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @Test
    public void onHandleIntent_withActiveProject() throws DomainException {
        getService().enableOngoingNotification();
        List<Project> projects = new ArrayList<>();
        projects.add(buildInactiveProjectWithId());
        projects.add(buildActiveProjectWithId(2L));
        when(getProjects.execute())
                .thenReturn(projects);

        serviceController.startCommand(0, 0);

        assertNotNull(nm.getNotification("2", WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @Test
    public void onHandleIntent_withActiveProjects() throws DomainException {
        getService().enableOngoingNotification();
        List<Project> projects = new ArrayList<>();
        projects.add(buildInactiveProjectWithId());
        projects.add(buildActiveProjectWithId(2L));
        projects.add(buildActiveProjectWithId(3L));
        when(getProjects.execute())
                .thenReturn(projects);

        serviceController.startCommand(0, 0);

        assertNotNull(nm.getNotification("2", WorkerApplication.NOTIFICATION_ON_GOING_ID));
        assertNotNull(nm.getNotification("3", WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @SuppressLint("Registered")
    public static class TestService extends ReloadNotificationService {
        private boolean isOngoingNotificationEnabled = false;
        private GetProjects getProjects;

        @Override
        public void onStart(Intent intent, int startId) {
            onHandleIntent(intent);
            stopSelf(startId);
        }

        @Override
        protected boolean isOngoingNotificationEnabled() {
            return isOngoingNotificationEnabled;
        }

        private void enableOngoingNotification() {
            isOngoingNotificationEnabled = true;
        }

        @Override
        protected GetProjects buildGetProjectsUseCase() {
            return getProjects;
        }
    }
}
