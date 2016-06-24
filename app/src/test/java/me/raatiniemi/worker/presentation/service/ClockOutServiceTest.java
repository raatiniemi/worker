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

package me.raatiniemi.worker.presentation.service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.greenrobot.eventbus.EventBus;
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

import java.util.Date;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.domain.exception.ClockActivityException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.Worker;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ClockOutServiceTest {
    private ServiceController<TestService> mServiceController;

    private NotificationManager mNotificationManager;
    private ClockOut mClockOut;
    private EventBus mEventBus;

    private Intent buildIntentForService() {
        return new Intent(
                RuntimeEnvironment.application,
                ClockOutService.class
        );
    }

    private Uri buildProjectDataUri() {
        return WorkerContract.ProjectContract.getItemUri(1L);
    }

    @Before
    public void setUp() {
        mServiceController = Robolectric.buildService(TestService.class);
        mServiceController.attach()
                .create()
                .get();

        setUpNotificationManager();
        setUpService();
    }

    private void setUpNotificationManager() {
        mNotificationManager = mock(NotificationManager.class);

        Application application = RuntimeEnvironment.application;
        ShadowContextImpl shadowContext = (ShadowContextImpl) Shadows.shadowOf(application.getBaseContext());
        shadowContext.setSystemService(Context.NOTIFICATION_SERVICE, mNotificationManager);
    }

    private void setUpService() {
        TestService service = getService();
        service.mClockOut = mClockOut = mock(ClockOut.class);
        service.mEventBus = mEventBus = mock(EventBus.class);
    }

    private TestService getService() {
        return mServiceController.get();
    }

    @After
    public void tearDown() {
        mServiceController.destroy();
    }

    @Test
    public void onHandleIntent_whenClockOutFail()
            throws DomainException {
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());

        doThrow(ClockActivityException.class)
                .when(mClockOut)
                .execute(eq(1L), isA(Date.class));

        mServiceController.withIntent(intent)
                .startCommand(0, 0);

        verify(mEventBus, never()).post(isA(OngoingNotificationActionEvent.class));
        verify(mNotificationManager).notify(
                eq("1"),
                eq(Worker.NOTIFICATION_ON_GOING_ID),
                isA(Notification.class)
        );
    }

    @Test
    public void onHandleIntent()
            throws DomainException {
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());

        mServiceController.withIntent(intent)
                .startCommand(0, 0);

        verify(mClockOut).execute(
                eq(1L),
                isA(Date.class)
        );
        verify(mEventBus).post(isA(OngoingNotificationActionEvent.class));
        verify(mNotificationManager).cancel(
                eq("1"),
                eq(Worker.NOTIFICATION_ON_GOING_ID)
        );
    }

    @SuppressLint("Registered")
    public static class TestService extends ClockOutService {
        private ClockOut mClockOut;
        private EventBus mEventBus;

        @Override
        public void onStart(Intent intent, int startId) {
            onHandleIntent(intent);
            stopSelf(startId);
        }

        @Override
        protected EventBus getEventBus() {
            return mEventBus;
        }

        @Override
        protected ClockOut buildClockOutUseCase() {
            return mClockOut;
        }
    }
}
