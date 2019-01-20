/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project.timesheet.view;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.raatiniemi.worker.Preferences;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.util.DigitalHoursMinutesIntervalFormat;
import me.raatiniemi.worker.domain.util.FractionIntervalFormat;
import me.raatiniemi.worker.domain.util.HoursMinutesFormat;
import me.raatiniemi.worker.features.project.ViewModels;
import me.raatiniemi.worker.features.project.timesheet.viewmodel.GetTimesheetViewModel;
import me.raatiniemi.worker.features.project.timesheet.viewmodel.RegisterTimesheetViewModel;
import me.raatiniemi.worker.features.project.timesheet.viewmodel.RemoveTimesheetViewModel;
import me.raatiniemi.worker.features.project.view.ProjectActivity;
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.features.shared.view.dialog.RxAlertDialog;
import me.raatiniemi.worker.features.shared.view.fragment.RxFragment;
import me.raatiniemi.worker.util.KeyValueStore;
import me.raatiniemi.worker.util.KeyValueStoreKt;
import me.raatiniemi.worker.util.SelectionListener;
import timber.log.Timber;

import static me.raatiniemi.worker.R.drawable.list_item_divider;
import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.RxUtil.applySchedulersWithBackpressureBuffer;

public class TimesheetFragment extends RxFragment implements SelectionListener {
    private final Preferences preferences = new Preferences();
    private final KeyValueStore keyValueStore = preferences.getKeyValueStore();

    private final ViewModels viewModels = new ViewModels();
    private final GetTimesheetViewModel.ViewModel getTimesheetViewModel = viewModels.getTimeSheet();
    private final RegisterTimesheetViewModel.ViewModel registerTimesheetViewModel = viewModels.getRegisterTimesheet();
    private final RemoveTimesheetViewModel.ViewModel removeTimesheetViewModel = viewModels.getRemoveTimesheet();

    private final EventBus eventBus = EventBus.getDefault();

    private LinearLayoutManager linearLayoutManager;

    private TimesheetAdapter adapter;

    private ActionMode actionMode;

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.setTitle(R.string.menu_title_actions);
            actionMode.getMenuInflater().inflate(R.menu.actions_project_timesheet, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.actions_project_timesheet_delete:
                    confirmRemoveSelectedItems(actionMode);
                    return false;

                case R.id.actions_project_timesheet_register:
                    toggleRegisterSelectedItems(actionMode);
                    return true;

                default:
                    Timber.w("Undefined action: %d", item.getItemId());
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            adapter.deselectItems();

            TimesheetFragment.this.actionMode = null;
        }

        private void confirmRemoveSelectedItems(ActionMode actionMode) {
            DeleteTimeDialog.show(getActivity())
                    .filter(RxAlertDialog::isPositive)
                    .subscribe(
                            which -> {
                                removeTimesheetViewModel.remove(adapter.getSelectedItems());

                                actionMode.finish();
                            },
                            Timber::w
                    );
        }

        private void toggleRegisterSelectedItems(ActionMode actionMode) {
            registerTimesheetViewModel.register(adapter.getSelectedItems());

            actionMode.finish();
        }
    };

    private boolean loading = false;

    public static TimesheetFragment newInstance(Bundle bundle) {
        TimesheetFragment timesheetFragment = new TimesheetFragment();
        timesheetFragment.setArguments(bundle);

        return timesheetFragment;
    }

    private long getProjectId() {
        return getArguments().getLong(ProjectActivity.MESSAGE_PROJECT_ID, -1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventBus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timesheet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TimesheetAdapter(getHoursMinutesFormat(), this);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        RecyclerViewExpandableItemManager recyclerViewExpandableItemManager
                = new RecyclerViewExpandableItemManager(savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.fragment_timesheet);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(recyclerViewExpandableItemManager.createWrappedAdapter(adapter));
        recyclerView.addItemDecoration(
                new SimpleListDividerDecorator(
                        getResources().getDrawable(
                                list_item_divider,
                                getActivity().getTheme()
                        ),
                        true
                )
        );
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Make sure we're not loading data before checking the position.
                if (!loading) {
                    // Retrieve positional data, needed for the calculation on whether
                    // we are close to the end of the list or not.
                    int visibleItems = linearLayoutManager.getChildCount();
                    int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();

                    // Retrieve the total number of items within the recycler view,
                    // this will include both the group and the children.
                    int totalItems = linearLayoutManager.getItemCount();

                    // Check if the last row in the list is visible.
                    if ((visibleItems + firstVisiblePosition) >= totalItems) {
                        // We are about to start loading data, and thus we need
                        // to block additional loading requests.
                        loading = true;

                        // Retrieve the total number of groups within the view, we need to
                        // exclude the children otherwise the offset will be wrong.
                        int offset = adapter.getGroupCount();

                        // Retrieve additional timesheet items with offset.
                        getTimesheetViewModel.fetch(getProjectId(), offset);
                    }
                }
            }
        });
        recyclerViewExpandableItemManager.attachRecyclerView(recyclerView);

        if (keyValueStore.hideRegisteredTime()) {
            getTimesheetViewModel.hideRegisteredTime();
        }

        getTimesheetViewModel.success()
                .compose(bindToLifecycle())
                .compose(applySchedulersWithBackpressureBuffer())
                .subscribe(
                        group -> {
                            adapter.add(group);

                            // TODO: Call `finishLoading` when all items in buffer have been added.
                            // The call to `finishLoading` will be called for each of the added
                            // groups, i.e. there's a window in where we can load the same segment
                            // multiple times due to the disconnect between finish loading and the
                            // user attempts scroll (causing another load to happen). However, this
                            // seems to be fairly theoretical, at least now, but should be improved.
                            finishLoading();
                        },
                        Timber::e
                );
        getTimesheetViewModel.errors()
                .compose(bindToLifecycle())
                .subscribe(e -> showGetTimesheetErrorMessage());
        registerTimesheetViewModel.success()
                .compose(bindToLifecycle())
                .compose(applySchedulersWithBackpressureBuffer())
                .subscribe(
                        result -> {
                            if (keyValueStore.hideRegisteredTime()) {
                                adapter.remove(result);
                                return;
                            }

                            adapter.set(result);
                        },
                        Timber::e
                );
        registerTimesheetViewModel.errors()
                .compose(bindToLifecycle())
                .subscribe(e -> showRegisterErrorMessage());
        removeTimesheetViewModel.success()
                .compose(bindToLifecycle())
                .compose(applySchedulersWithBackpressureBuffer())
                .subscribe(
                        result -> adapter.remove(result),
                        Timber::e
                );
        removeTimesheetViewModel.errors()
                .compose(bindToLifecycle())
                .subscribe(e -> showDeleteErrorMessage());

        getTimesheetViewModel.fetch(getProjectId(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        eventBus.unregister(this);
    }

    @NonNull
    private HoursMinutesFormat getHoursMinutesFormat() {
        int format = keyValueStore.timeSheetSummaryFormat();
        if (KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_FRACTION == format) {
            return new FractionIntervalFormat();
        }

        return new DigitalHoursMinutesIntervalFormat();
    }

    private void showGetTimesheetErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_get_time_report,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void showDeleteErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_delete_time_report,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void showRegisterErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_register_time_report,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void finishLoading() {
        loading = false;
    }

    public void refresh() {
        if (keyValueStore.hideRegisteredTime()) {
            getTimesheetViewModel.hideRegisteredTime();
        } else {
            getTimesheetViewModel.showRegisteredTime();
        }

        // Clear the items from the list and start loading from the beginning...
        adapter.clear();
        getTimesheetViewModel.fetch(getProjectId(), 0);
    }

    @Override
    public void onSelect() {
        if (isNull(actionMode)) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
    }

    @Override
    public void onDeselect() {
        if (isNull(actionMode)) {
            return;
        }

        if (adapter.haveSelectedItems()) {
            return;
        }

        actionMode.finish();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OngoingNotificationActionEvent event) {
        if (event.getProjectId() == getProjectId()) {
            refresh();
            return;
        }

        Timber.d("No need to refresh, event is related to another project");
    }
}
