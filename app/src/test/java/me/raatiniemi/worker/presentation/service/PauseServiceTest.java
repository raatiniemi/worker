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
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.Worker;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PauseServiceTest {
    private ServiceController<TestService> mServiceController;

    private NotificationManager mNotificationManager;
    private ClockOut mClockOut;
    private GetProject mGetProject;
    private EventBus mEventBus;

    private Intent buildIntentForService() {
        return new Intent(
                RuntimeEnvironment.application,
                PauseService.class
        );
    }

    private Uri buildProjectDataUri() {
        return WorkerContract.ProjectContract.getItemUri(1L);
    }

    private Project buildProject(long projectId)
            throws InvalidProjectNameException {
        return new Project.Builder("Project name")
                .id(projectId)
                .build();
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
        service.mGetProject = mGetProject = mock(GetProject.class);
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
    public void onHandleIntent_withOngoingNotificationEnabled()
            throws DomainException {
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());
        getService().enableOngoingNotification();

        when(
                mGetProject.execute(1L)
        ).thenReturn(buildProject(1L));

        mServiceController.withIntent(intent)
                .startCommand(0, 0);

        verify(mClockOut).execute(
                eq(1L),
                isA(Date.class)
        );
        verify(mEventBus).post(isA(OngoingNotificationActionEvent.class));
        verify(mGetProject).execute(eq(1L));
        verify(mNotificationManager).notify(
                eq("1"),
                eq(Worker.NOTIFICATION_ON_GOING_ID),
                isA(Notification.class)
        );

        verify(mNotificationManager, never())
                .cancel(anyString(), anyInt());
    }

    @Test
    public void onHandleIntent_withOngoingNotificationDisabled()
            throws DomainException {
        Intent intent = buildIntentForService();
        intent.setData(buildProjectDataUri());

        when(
                mGetProject.execute(1L)
        ).thenReturn(buildProject(1L));

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

        verify(mNotificationManager, never()).notify(
                anyString(),
                anyInt(),
                any(Notification.class)
        );
    }

    public static class TestService extends PauseService {
        private ClockOut mClockOut;
        private GetProject mGetProject;
        private EventBus mEventBus;
        private boolean mIsOngoingNotificationEnabled = false;

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
        protected boolean isOngoingNotificationEnabled() {
            return mIsOngoingNotificationEnabled;
        }

        private void enableOngoingNotification() {
            mIsOngoingNotificationEnabled = true;
        }

        @Override
        protected ClockOut buildClockOutUseCase() {
            return mClockOut;
        }

        @Override
        protected GetProject buildGetProjectUseCase() {
            return mGetProject;
        }
    }
}
