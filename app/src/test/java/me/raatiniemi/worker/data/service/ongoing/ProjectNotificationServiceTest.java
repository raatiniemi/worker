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
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowContextImpl;
import org.robolectric.util.ServiceController;

import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.interactor.IsProjectActive;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.RobolectricTestCase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectNotificationServiceTest extends RobolectricTestCase {
    private ServiceController<TestService> serviceController;

    private NotificationManager notificationManager;
    private IsProjectActive isProjectActive;
    private GetProject getProject;

    private Intent buildIntentForService() {
        return new Intent(
                RuntimeEnvironment.application,
                ClockOutService.class
        );
    }

    private Uri buildProjectDataUri() {
        return ProviderContract.Project.getItemUri(1L);
    }

    private TestService getService() {
        return serviceController.get();
    }

    @Before
    public void setUp() {
        serviceController = Robolectric.buildService(TestService.class);
        serviceController.attach()
                .create()
                .get();

        setUpService();
        notificationManager = buildNotificationManager();
    }

    private void setUpService() {
        isProjectActive = mock(IsProjectActive.class);
        getProject = mock(GetProject.class);

        TestService service = getService();
        service.isProjectActive = isProjectActive;
        service.getProject = getProject;
    }

    private NotificationManager buildNotificationManager() {
        NotificationManager notificationManager = mock(NotificationManager.class);

        Application application = RuntimeEnvironment.application;
        ShadowContextImpl shadowContext = (ShadowContextImpl) Shadows.shadowOf(application.getBaseContext());
        shadowContext.setSystemService(Context.NOTIFICATION_SERVICE, notificationManager);

        return notificationManager;
    }

    @After
    public void tearDown() {
        serviceController.destroy();
    }

    @Test
    public void onHandleIntent_withOngoingNotificationDisabled() throws DomainException {
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());

        serviceController.withIntent(intent)
                .startCommand(0, 0);

        verify(getProject, never()).execute(eq(1L));
        verify(notificationManager, never())
                .notify(anyInt(), any(Notification.class));
    }

    @Test
    public void onHandleIntent_withInactiveProject() throws DomainException {
        getService().enableOngoingNotification();
        when(isProjectActive.execute(eq(1L)))
                .thenReturn(false);
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());

        serviceController.withIntent(intent)
                .startCommand(0, 0);

        verify(isProjectActive).execute(eq(1L));
        verify(getProject, never()).execute(anyInt());
        verify(notificationManager)
                .cancel(eq("1"), eq(WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @Test
    public void onHandleIntent_withActiveProject() throws DomainException {
        getService().enableOngoingNotification();
        when(isProjectActive.execute(eq(1L)))
                .thenReturn(true);
        when(getProject.execute(eq(1L)))
                .thenReturn(
                        Project.builder("Project")
                                .id(1L)
                                .build()
                );
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());

        serviceController.withIntent(intent)
                .startCommand(0, 0);

        verify(isProjectActive).execute(eq(1L));
        verify(getProject).execute(eq(1L));
        verify(notificationManager)
                .notify(
                        eq("1"),
                        eq(WorkerApplication.NOTIFICATION_ON_GOING_ID),
                        isA(Notification.class)
                );
    }

    @SuppressLint("Registered")
    public static class TestService extends ProjectNotificationService {
        private boolean isOngoingNotificationEnabled = false;
        private IsProjectActive isProjectActive;
        private GetProject getProject;

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
        IsProjectActive buildIsProjectActiveUseCase() {
            return isProjectActive;
        }

        @Override
        GetProject buildGetProjectUseCase() {
            return getProject;
        }
    }
}
