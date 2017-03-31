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
import android.support.annotation.NonNull;
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

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.presentation.project.model.TimeInAdapterResult;
import me.raatiniemi.worker.presentation.project.model.TimesheetGroupItem;
import me.raatiniemi.worker.presentation.project.presenter.TimesheetPresenter;
import me.raatiniemi.worker.presentation.util.SelectionListener;
import me.raatiniemi.worker.presentation.view.dialog.RxDialog;
import me.raatiniemi.worker.presentation.view.fragment.BaseFragment;
import timber.log.Timber;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.R.drawable.list_item_divider;
import static me.raatiniemi.worker.presentation.util.PresenterUtil.detachViewIfNotNull;

public class TimesheetFragment extends BaseFragment
        implements SelectionListener, TimesheetView {
    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @Inject
    TimesheetPresenter presenter;

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
            boolean finish = false;

            switch (item.getItemId()) {
                case R.id.actions_project_timesheet_delete:
                    DeleteTimeDialog.show(getActivity())
                            .filter(RxDialog::isPositive)
                            .subscribe(
                                    __ -> {
                                        presenter.remove(adapter.getSelectedItems());

                                        actionMode.finish();
                                    },
                                    Timber::w
                            );

                    // Since we are waiting for the user to confirm the action, we are unable to
                    // finish the action here.
                    finish = false;
                    break;
                case R.id.actions_project_timesheet_register:
                    presenter.register(adapter.getSelectedItems());
                    finish = true;
                    break;
                default:
                    Timber.w("Undefined action: %d", item.getItemId());
                    break;
            }

            if (finish) {
                actionMode.finish();
            }
            return finish;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            adapter.deselectItems();

            TimesheetFragment.this.actionMode = null;
        }
    };

    private boolean loading = false;

    public static TimesheetFragment newInstance(Bundle bundle) {
        TimesheetFragment timesheetFragment = new TimesheetFragment();
        timesheetFragment.setArguments(bundle);

        return timesheetFragment;
    }

    @Override
    public long getProjectId() {
        return getArguments().getLong(ProjectActivity.MESSAGE_PROJECT_ID, -1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Worker) getActivity().getApplication())
                .getProjectComponent()
                .inject(this);
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
                        presenter.getTimesheet(getProjectId(), offset);
                    }
                }
            }
        });
        recyclerViewExpandableItemManager.attachRecyclerView(recyclerView);

        presenter.attachView(this);
        presenter.getTimesheet(getProjectId(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        detachViewIfNotNull(presenter);
    }

    @Override
    public void add(@NonNull List<TimesheetGroupItem> groupItems) {
        adapter.add(groupItems);
    }

    @Override
    public void update(List<TimeInAdapterResult> results) {
        adapter.set(results);
    }

    @Override
    public void remove(List<TimeInAdapterResult> results) {
        adapter.remove(results);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showGetTimesheetErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_get_timesheet,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showDeleteErrorMessage(int numberOfItems) {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                getResources().getQuantityText(
                        R.plurals.error_message_delete_timesheet,
                        numberOfItems
                ),
                Snackbar.LENGTH_SHORT
        ).show();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showRegisterErrorMessage(int numberOfItems) {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                getResources().getQuantityText(
                        R.plurals.error_message_register_timesheet,
                        numberOfItems
                ),
                Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void finishLoading() {
        loading = false;
    }

    @Override
    public void refresh() {
        // Clear the items from the list and start loading from the beginning...
        adapter.clear();
        presenter.getTimesheet(getProjectId(), 0);
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
}
