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

package me.raatiniemi.worker;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class RxSchedulerRule implements TestRule {
    private static final TestScheduler sTestScheduler = new TestScheduler();

    private final RxJavaSchedulersHook mRxJavaSchedulersHook = new RxJavaSchedulersHook() {
        @Override
        public Scheduler getIOScheduler() {
            return Schedulers.immediate();
        }

        @Override
        public Scheduler getNewThreadScheduler() {
            return sTestScheduler;
        }
    };

    private final RxAndroidSchedulersHook mRxAndroidSchedulersHook = new RxAndroidSchedulersHook() {
        @Override
        public Scheduler getMainThreadScheduler() {
            return Schedulers.immediate();
        }
    };

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                RxJavaPlugins.getInstance().reset();
                RxJavaPlugins.getInstance().registerSchedulersHook(mRxJavaSchedulersHook);

                RxAndroidPlugins.getInstance().reset();
                RxAndroidPlugins.getInstance().registerSchedulersHook(mRxAndroidSchedulersHook);

                base.evaluate();

                RxJavaPlugins.getInstance().reset();
                RxAndroidPlugins.getInstance().reset();
            }
        };
    }

    public void advanceTimeTo(long delayTime, TimeUnit unit) {
        sTestScheduler.advanceTimeTo(delayTime, unit);
    }
}
