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

package me.raatiniemi.worker.presentation.project.presenter;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.project.model.TimesheetAdapterResult;
import me.raatiniemi.worker.presentation.project.view.TimesheetView;
import me.raatiniemi.worker.presentation.util.HideRegisteredTimePreferences;
import me.raatiniemi.worker.presentation.util.RxUtil;
import me.raatiniemi.worker.util.Optional;
import rx.Observable;
import timber.log.Timber;

public class TimesheetPresenter extends BasePresenter<TimesheetView> {
    private final HideRegisteredTimePreferences hideRegisteredTimePreferences;

    /**
     * Use case for marking time as registered.
     */
    private final MarkRegisteredTime markRegisteredTime;

    /**
     * Constructor.
     *
     * @param hideRegisteredTimePreferences Preferences for hide registered time.
     * @param markRegisteredTime            Use case for marking time as registered.
     */
    public TimesheetPresenter(
            HideRegisteredTimePreferences hideRegisteredTimePreferences,
            MarkRegisteredTime markRegisteredTime
    ) {
        this.hideRegisteredTimePreferences = hideRegisteredTimePreferences;
        this.markRegisteredTime = markRegisteredTime;
    }

    public void register(List<TimesheetAdapterResult> results) {
        final int numberOfItems = results.size();

        // TODO: Refactor to use optimistic propagation.
        Observable.just(results)
                .flatMap(this::registerTimeViaUseCase)
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        items -> {
                            Timber.d("register onNext");

                            performWithView(view -> {
                                if (hideRegisteredTimePreferences.shouldHideRegisteredTime()) {
                                    view.remove(items);
                                    return;
                                }

                                view.update(items);
                            });
                        },
                        e -> {
                            Timber.d("register onError");

                            // Log the error even if the view have been detached.
                            Timber.w(e, "Failed to mark time as registered");
                            performWithView(view -> view.showRegisterErrorMessage(numberOfItems));
                        },
                        () -> Timber.d("register onCompleted")
                );
    }

    private Observable<List<TimesheetAdapterResult>> registerTimeViaUseCase(List<TimesheetAdapterResult> results) {
        List<Time> timeToUpdate = new ArrayList<>();
        // noinspection Convert2streamapi
        for (TimesheetAdapterResult result : results) {
            timeToUpdate.add(result.getTime());
        }

        try {
            List<Time> updates = markRegisteredTime.execute(timeToUpdate);

            return Observable.just(mapUpdatesToPositionOfSelectedItems(updates, results));
        } catch (DomainException e) {
            return Observable.error(e);
        }
    }

    private static List<TimesheetAdapterResult> mapUpdatesToPositionOfSelectedItems(
            List<Time> updates,
            List<TimesheetAdapterResult> selectedItems
    ) {
        List<TimesheetAdapterResult> newResults = new ArrayList<>();

        for (TimesheetAdapterResult selectedItem : selectedItems) {
            Optional<Time> value = findUpdateForSelectedItem(selectedItem, updates);
            if (value.isPresent()) {
                TimesheetItem timesheetItem = TimesheetItem.with(value.get());
                newResults.add(TimesheetAdapterResult.build(selectedItem, timesheetItem));
            }
        }

        return newResults;
    }

    private static Optional<Time> findUpdateForSelectedItem(TimesheetAdapterResult selectedItem, List<Time> updates) {
        Time previousTime = selectedItem.getTime();

        for (Time update : updates) {
            if (update.getId().equals(previousTime.getId())) {
                return Optional.of(update);
            }
        }

        return Optional.empty();
    }
}
