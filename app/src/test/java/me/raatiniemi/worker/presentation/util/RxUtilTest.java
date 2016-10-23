package me.raatiniemi.worker.presentation.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import rx.Subscription;

import static me.raatiniemi.worker.presentation.util.RxUtil.unsubscribeIfNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RxUtilTest {
    @Test
    public void unsubscribeIfNotNull_withUnsubscribedSubscription() {
        Subscription subscription = mock(Subscription.class);
        when(subscription.isUnsubscribed()).thenReturn(true);

        unsubscribeIfNotNull(subscription);

        verify(subscription, never()).unsubscribe();
    }

    @Test
    public void unsubscribeIfNotNull_withActiveSubscription() {
        Subscription subscription = mock(Subscription.class);
        when(subscription.isUnsubscribed()).thenReturn(false);

        unsubscribeIfNotNull(subscription);

        verify(subscription).unsubscribe();
    }
}
