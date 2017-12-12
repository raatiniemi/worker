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

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.shadows.ShadowNotificationManager;

import java.util.Date;

import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.data.provider.ProviderContract;
import me.raatiniemi.worker.domain.exception.ClockActivityException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

public class ClockOutServiceTest extends RobolectricTestCase {
    private final ShadowNotificationManager nm = shadowOf((NotificationManager) RuntimeEnvironment
            .application
            .getSystemService(Context.NOTIFICATION_SERVICE));

    private ServiceController<TestService> serviceController;

    private ClockOut clockOut;
    private EventBus eventBus;

    private Intent buildIntentForService() {
        return new Intent(
                RuntimeEnvironment.application,
                ClockOutService.class
        );
    }

    private Uri buildProjectDataUri() {
        return ProviderContract.getProjectItemUri(1L);
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
        service.clockOut = clockOut = mock(ClockOut.class);
        service.eventBus = eventBus = mock(EventBus.class);
    }

    private TestService getService() {
        return serviceController.get();
    }

    @After
    public void tearDown() {
        serviceController.destroy();
    }

    @Test
    public void onHandleIntent_whenClockOutFail()
            throws DomainException {
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());

        doThrow(ClockActivityException.class)
                .when(clockOut)
                .execute(eq(1L), isA(Date.class));

        serviceController.withIntent(intent)
                .startCommand(0, 0);

        verify(eventBus, never()).post(isA(OngoingNotificationActionEvent.class));
        assertNotNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @Test
    public void onHandleIntent()
            throws DomainException {
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());

        serviceController.withIntent(intent)
                .startCommand(0, 0);

        verify(clockOut).execute(
                eq(1L),
                isA(Date.class)
        );
        verify(eventBus).post(isA(OngoingNotificationActionEvent.class));
        assertNull(nm.getNotification("1", WorkerApplication.NOTIFICATION_ON_GOING_ID));
    }

    @SuppressLint("Registered")
    public static class TestService extends ClockOutService {
        private ClockOut clockOut;
        private EventBus eventBus;

        @Override
        public void onStart(Intent intent, int startId) {
            onHandleIntent(intent);
            stopSelf(startId);
        }

        @Override
        protected EventBus getEventBus() {
            return eventBus;
        }

        @Override
        protected ClockOut buildClockOutUseCase() {
            return clockOut;
        }
    }
}
