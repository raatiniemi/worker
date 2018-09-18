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

package me.raatiniemi.worker.features.projects.viewmodel;

import android.support.annotation.NonNull;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.features.projects.model.ProjectsItem;
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static me.raatiniemi.worker.util.RxUtil.hideErrors;
import static me.raatiniemi.worker.util.RxUtil.redirectErrors;

public interface ClockActivityViewModel {
    interface Input {
        void startingPointForTimeSummary(int startingPoint);

        void clockIn(@NonNull ProjectsItemAdapterResult result, @NonNull Date date);

        void clockOut(@NonNull ProjectsItemAdapterResult result, @NonNull Date date);
    }

    interface Output {
        @NonNull
        Observable<ProjectsItemAdapterResult> clockInSuccess();

        @NonNull
        Observable<ProjectsItemAdapterResult> clockOutSuccess();
    }

    interface Error {
        @NonNull
        Observable<Throwable> clockInError();

        @NonNull
        Observable<Throwable> clockOutError();
    }

    class ViewModel implements Input, Output, Error {
        private final Input input;
        private final Output output;
        private final Error error;

        private int startingPoint = GetProjectTimeSince.MONTH;

        private final PublishSubject<ProjectsItemAdapterResult> clockInResult = PublishSubject.create();
        private final PublishSubject<Date> clockInDate = PublishSubject.create();

        private final PublishSubject<ProjectsItemAdapterResult> clockInSuccess = PublishSubject.create();
        private final PublishSubject<Throwable> clockInError = PublishSubject.create();

        private final PublishSubject<ProjectsItemAdapterResult> clockOutResult = PublishSubject.create();
        private final PublishSubject<Date> clockOutDate = PublishSubject.create();

        private final PublishSubject<ProjectsItemAdapterResult> clockOutSuccess = PublishSubject.create();
        private final PublishSubject<Throwable> clockOutError = PublishSubject.create();

        private final ClockActivityChange clockActivityChange;
        private final GetProjectTimeSince getProjectTimeSince;

        public ViewModel(
                @NonNull ClockActivityChange clockActivityChange,
                @NonNull GetProjectTimeSince getProjectTimeSince
        ) {
            input = this;
            output = this;
            error = this;

            this.clockActivityChange = clockActivityChange;
            this.getProjectTimeSince = getProjectTimeSince;

            Observable.zip(clockInResult, clockInDate, CombinedResult::new)
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .switchMap(result -> executeUseCase(result)
                            .compose(redirectErrors(clockInError))
                            .compose(hideErrors())
                    )
                    .subscribe(clockInSuccess);

            Observable.zip(clockOutResult, clockOutDate, CombinedResult::new)
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .switchMap(result -> executeUseCase(result)
                            .compose(redirectErrors(clockOutError))
                            .compose(hideErrors())
                    )
                    .subscribe(clockOutSuccess);
        }

        @NonNull
        @RxLogObservable
        private Observable<ProjectsItemAdapterResult> executeUseCase(@NonNull CombinedResult combinedResult) {
            try {
                Project project = executeClockActivityChange(combinedResult);
                List<Time> registeredTime = getRegisteredTimeForProject(project);

                ProjectsItem projectsItem = new ProjectsItem(project, registeredTime);
                return Observable.just(buildResult(combinedResult.result, projectsItem));
            } catch (Exception e) {
                return Observable.error(e);
            }
        }

        @NonNull
        private Project executeClockActivityChange(@NonNull CombinedResult combinedResult) throws DomainException {
            ProjectsItem projectsItem = combinedResult.result.getProjectsItem();

            return clockActivityChange.execute(projectsItem.asProject(), combinedResult.date);
        }

        @NonNull
        private List<Time> getRegisteredTimeForProject(@NonNull Project project) throws DomainException {
            return getProjectTimeSince.execute(project, startingPoint);
        }

        @NonNull
        private static ProjectsItemAdapterResult buildResult(
                @NonNull ProjectsItemAdapterResult result,
                @NonNull ProjectsItem projectsItem
        ) {
            return ProjectsItemAdapterResult.build(result.getPosition(), projectsItem);
        }

        @Override
        public void clockIn(@NonNull ProjectsItemAdapterResult result, @NonNull Date date) {
            this.clockInResult.onNext(result);
            this.clockInDate.onNext(date);
        }

        @Override
        public void clockOut(@NonNull ProjectsItemAdapterResult result, @NonNull Date date) {
            this.clockOutResult.onNext(result);
            this.clockOutDate.onNext(date);
        }

        @NonNull
        @Override
        @RxLogObservable
        public Observable<ProjectsItemAdapterResult> clockInSuccess() {
            return clockInSuccess;
        }

        @NonNull
        @Override
        @RxLogObservable
        public Observable<ProjectsItemAdapterResult> clockOutSuccess() {
            return clockOutSuccess;
        }

        @Override
        public void startingPointForTimeSummary(int startingPoint) {
            switch (startingPoint) {
                case GetProjectTimeSince.MONTH:
                case GetProjectTimeSince.WEEK:
                case GetProjectTimeSince.DAY:
                    this.startingPoint = startingPoint;
                    break;
                default:
                    Timber.d("Invalid starting point supplied: %i", startingPoint);
            }
        }

        @NonNull
        @Override
        @RxLogObservable
        public Observable<Throwable> clockInError() {
            return clockInError;
        }

        @NonNull
        @Override
        @RxLogObservable
        public Observable<Throwable> clockOutError() {
            return clockOutError;
        }

        @NonNull
        public Input input() {
            return input;
        }

        @NonNull
        public Output output() {
            return output;
        }

        @NonNull
        public Error error() {
            return error;
        }

        private static class CombinedResult {
            private final ProjectsItemAdapterResult result;
            private final Date date;

            private CombinedResult(@NonNull ProjectsItemAdapterResult result, @NonNull Date date) {
                this.result = result;
                this.date = date;
            }
        }
    }
}
