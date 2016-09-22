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

package me.raatiniemi.worker.data.service.ongoing;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContextImpl;
import org.robolectric.util.ServiceController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ReloadNotificationServiceTest {
    private ServiceController<TestService> serviceController;

    private NotificationManager notificationManager;
    private GetProjects getProjects;

    private Project buildActiveProjectWithId(Long projectId) throws DomainException {
        return buildProjectWithIdAndStatus(projectId, true);
    }

    private Project buildInactiveProjectWithId(Long projectId) throws DomainException {
        return buildProjectWithIdAndStatus(projectId, false);
    }

    private Project buildProjectWithIdAndStatus(Long projectId, boolean isActive)
            throws DomainException {
        Project project = new Project.Builder("Project name")
                .id(projectId)
                .build();

        if (isActive) {
            Time time = new Time.Builder(projectId)
                    .startInMilliseconds(1L)
                    .build();

            List<Time> times = new ArrayList<>();
            times.add(time);

            project.addTime(times);
        }

        return project;
    }

    @Before
    public void setUp() {
        serviceController = Robolectric.buildService(TestService.class);
        serviceController.attach()
                .create()
                .get();

        setUpNotificationManager();
        setUpService();
    }

    private void setUpNotificationManager() {
        notificationManager = mock(NotificationManager.class);

        Application application = RuntimeEnvironment.application;
        ShadowContextImpl shadowContext = (ShadowContextImpl) Shadows.shadowOf(application.getBaseContext());
        shadowContext.setSystemService(Context.NOTIFICATION_SERVICE, notificationManager);
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
        verify(notificationManager, never())
                .notify(anyInt(), any(Notification.class));
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
        verify(notificationManager, never())
                .notify(anyInt(), any(Notification.class));
    }

    @Test
    public void onHandleIntent_withoutProjects() throws DomainException {
        getService().enableOngoingNotification();
        when(getProjects.execute())
                .thenReturn(Collections.emptyList());

        serviceController.startCommand(0, 0);

        verify(notificationManager, never())
                .notify(anyInt(), any(Notification.class));
    }

    @Test
    public void onHandleIntent_withoutActiveProjects() throws DomainException {
        getService().enableOngoingNotification();
        List<Project> projects = new ArrayList<>();
        projects.add(buildInactiveProjectWithId(1L));
        when(getProjects.execute())
                .thenReturn(projects);

        serviceController.startCommand(0, 0);

        verify(notificationManager, never())
                .notify(anyInt(), any(Notification.class));
    }

    @Test
    public void onHandleIntent_withActiveProject() throws DomainException {
        getService().enableOngoingNotification();
        List<Project> projects = new ArrayList<>();
        projects.add(buildInactiveProjectWithId(1L));
        projects.add(buildActiveProjectWithId(2L));
        when(getProjects.execute())
                .thenReturn(projects);

        serviceController.startCommand(0, 0);

        verify(notificationManager)
                .notify(
                        eq("2"),
                        eq(Worker.NOTIFICATION_ON_GOING_ID),
                        isA(Notification.class)
                );
    }

    @Test
    public void onHandleIntent_withActiveProjects() throws DomainException {
        getService().enableOngoingNotification();
        List<Project> projects = new ArrayList<>();
        projects.add(buildInactiveProjectWithId(1L));
        projects.add(buildActiveProjectWithId(2L));
        projects.add(buildActiveProjectWithId(3L));
        when(getProjects.execute())
                .thenReturn(projects);

        serviceController.startCommand(0, 0);

        verify(notificationManager)
                .notify(
                        eq("2"),
                        eq(Worker.NOTIFICATION_ON_GOING_ID),
                        isA(Notification.class)
                );
        verify(notificationManager)
                .notify(
                        eq("3"),
                        eq(Worker.NOTIFICATION_ON_GOING_ID),
                        isA(Notification.class)
                );
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
