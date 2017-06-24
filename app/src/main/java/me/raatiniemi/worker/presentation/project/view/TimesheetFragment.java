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

package me.raatiniemi.worker.presentation.project.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.project.viewmodel.GetTimesheetViewModel;
import me.raatiniemi.worker.presentation.project.viewmodel.RegisterTimesheetViewModel;
import me.raatiniemi.worker.presentation.project.viewmodel.RemoveTimesheetViewModel;
import me.raatiniemi.worker.presentation.util.HideRegisteredTimePreferences;
import me.raatiniemi.worker.presentation.util.SelectionListener;
import me.raatiniemi.worker.presentation.view.dialog.RxAlertDialog;
import me.raatiniemi.worker.presentation.view.fragment.RxFragment;
import timber.log.Timber;

import static me.raatiniemi.worker.R.drawable.list_item_divider;
import static me.raatiniemi.worker.presentation.util.RxUtil.applySchedulers;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public class TimesheetFragment extends RxFragment implements SelectionListener {
    @Inject
    HideRegisteredTimePreferences hideRegisteredTimePreferences;

    @Inject
    GetTimesheetViewModel.ViewModel getTimesheetViewModel;

    @Inject
    RegisterTimesheetViewModel.ViewModel registerTimesheetViewModel;

    @Inject
    RemoveTimesheetViewModel.ViewModel removeTimesheetViewModel;

    @Inject
    EventBus eventBus;

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

        ((WorkerApplication) getActivity().getApplication())
                .getProjectComponent()
                .inject(this);

        eventBus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timesheet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TimesheetAdapter(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        RecyclerViewExpandableItemManager recyclerViewExpandableItemManager
                = new RecyclerViewExpandableItemManager(savedInstanceState);

        RecyclerView recyclerView = ButterKnife.findById(view, R.id.fragment_timesheet);
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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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

        if (hideRegisteredTimePreferences.shouldHideRegisteredTime()) {
            getTimesheetViewModel.hideRegisteredTime();
        }

        getTimesheetViewModel.success()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(
                        group -> adapter.add(group),
                        e -> {
                        },
                        // TODO: Improve infinite scrolling.
                        this::finishLoading
                );
        getTimesheetViewModel.errors()
                .compose(bindToLifecycle())
                .subscribe(e -> showGetTimesheetErrorMessage());
        registerTimesheetViewModel.success()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(result -> {
                    if (hideRegisteredTimePreferences.shouldHideRegisteredTime()) {
                        adapter.remove(result);
                        return;
                    }

                    adapter.set(result);
                });
        registerTimesheetViewModel.errors()
                .compose(bindToLifecycle())
                .subscribe(e -> showRegisterErrorMessage());
        removeTimesheetViewModel.success()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(result -> adapter.remove(result));
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

    private void showGetTimesheetErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_get_timesheet,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void showDeleteErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_delete_timesheet,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void showRegisterErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_register_timesheet,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void finishLoading() {
        loading = false;
    }

    void refresh() {
        if (hideRegisteredTimePreferences.shouldHideRegisteredTime()) {
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
