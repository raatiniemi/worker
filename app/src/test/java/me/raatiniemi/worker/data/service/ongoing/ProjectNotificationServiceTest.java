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
import android.net.Uri;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.shadows.ShadowNotificationManager;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.interactor.IsProjectActive;
import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class ProjectNotificationServiceTest extends RobolectricTestCase {
    private final ShadowNotificationManager nm = shadowOf((NotificationManager) RuntimeEnvironment
            .application
            .getSystemService(Context.NOTIFICATION_SERVICE));

    private ServiceController<TestService> serviceController;

    private IsProjectActive isProjectActive;
    private GetProject getProject;

    private Intent buildIntentForService() {
        return new Intent(
                RuntimeEnvironment.application,
                ClockOutService.class
        );
    }

    private Uri buildProjectDataUri() {
        return ProviderContract.getProjectItemUri(1L);
    }

    private TestService getService() {
        return serviceController.get();
    }

    @Before
    public void setUp() {
        serviceController = Robolectric.buildService(TestService.class);
        serviceController.create()
                .get();

        setUpService();
    }

    private void setUpService() {
        isProjectActive = mock(IsProjectActive.class);
        getProject = mock(GetProject.class);

        TestService service = getService();
        service.isProjectActive = isProjectActive;
        service.getProject = getProject;
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
        assertNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
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
        assertNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
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
        assertNotNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
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
