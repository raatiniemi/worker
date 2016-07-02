/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.presentation.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.interactor.GetTimesheet;
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime;
import me.raatiniemi.worker.domain.interactor.RemoveTime;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.base.view.fragment.MvpFragment;
import me.raatiniemi.worker.presentation.model.timesheet.TimeInAdapterResult;
import me.raatiniemi.worker.presentation.model.timesheet.TimesheetGroupModel;
import me.raatiniemi.worker.presentation.presenter.TimesheetPresenter;
import me.raatiniemi.worker.presentation.util.SelectionListener;
import me.raatiniemi.worker.presentation.view.TimesheetView;
import me.raatiniemi.worker.presentation.view.adapter.TimesheetAdapter;

import static me.raatiniemi.worker.R.drawable.list_item_divider;

public class TimesheetFragment extends MvpFragment<TimesheetPresenter>
        implements SelectionListener, TimesheetView {
    private static final String TAG = "TimesheetFragment";

    private LinearLayoutManager mLinearLayoutManager;

    private TimesheetAdapter mAdapter;

    private ActionMode mActionMode;

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
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
                    // Before we remove the time, the user have to confirm this
                    // action since the remove and register is fairly close.
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.confirm_delete_time_title)
                            .setMessage(R.string.confirm_delete_time_message)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getPresenter().remove(getAdapter().getSelectedItems());

                                    // Since the item have been removed, we can finish the action.
                                    actionMode.finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();

                    // Since we are waiting for the user to confirm the action, we are unable to
                    // finish the action here.
                    finish = false;
                    break;
                case R.id.actions_project_timesheet_register:
                    getPresenter().register(getAdapter().getSelectedItems());
                    finish = true;
                    break;
                default:
                    Log.w(TAG, "Undefined action: " + item.getItemId());
                    break;
            }

            if (finish) {
                actionMode.finish();
            }
            return finish;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            getAdapter().deselectItems();

            mActionMode = null;
        }
    };

    private boolean mLoading = false;

    private long getProjectId() {
        return getArguments().getLong(ProjectsFragment.MESSAGE_PROJECT_ID, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timesheet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        RecyclerViewExpandableItemManager recyclerViewExpandableItemManager
                = new RecyclerViewExpandableItemManager(savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_timesheet);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(recyclerViewExpandableItemManager.createWrappedAdapter(getAdapter()));
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
                if (!mLoading) {
                    // Retrieve positional data, needed for the calculation on whether
                    // we are close to the end of the list or not.
                    int visibleItems = mLinearLayoutManager.getChildCount();
                    int firstVisiblePosition = mLinearLayoutManager.findFirstVisibleItemPosition();

                    // Retrieve the total number of items within the recycler view,
                    // this will include both the group and the children.
                    int totalItems = mLinearLayoutManager.getItemCount();

                    // Check if the last row in the list is visible.
                    if ((visibleItems + firstVisiblePosition) >= totalItems) {
                        // We are about to start loading data, and thus we need
                        // to block additional loading requests.
                        mLoading = true;

                        // Retrieve the total number of groups within the view, we need to
                        // exclude the children otherwise the offset will be wrong.
                        int offset = getAdapter().getGroupCount();

                        // Retrieve additional timesheet items with offset.
                        getPresenter().getTimesheet(getProjectId(), offset);
                    }
                }
            }
        });
        recyclerViewExpandableItemManager.attachRecyclerView(recyclerView);

        getPresenter().attachView(this);
        getPresenter().getTimesheet(getProjectId(), 0);
    }

    @Override
    protected TimesheetPresenter createPresenter() {
        // Create the time repository.
        TimeRepository timeRepository = new TimeResolverRepository(
                getActivity().getContentResolver(),
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );

        return new TimesheetPresenter(
                getActivity(),
                EventBus.getDefault(),
                getProjectId(),
                new GetTimesheet(timeRepository),
                new MarkRegisteredTime(timeRepository),
                new RemoveTime(timeRepository)
        );
    }

    @NonNull
    public TimesheetAdapter getAdapter() {
        if (null == mAdapter) {
            mAdapter = new TimesheetAdapter(this);
        }

        return mAdapter;
    }

    @NonNull
    public TimesheetGroupModel get(int index) {
        return getAdapter().get(index);
    }

    public void set(int index, @NonNull TimesheetGroupModel item) {
        getAdapter().set(index, item);
    }

    public int add(@NonNull TimesheetGroupModel item) {
        return getAdapter().add(item);
    }

    public int add(@NonNull List<TimesheetGroupModel> items) {
        return getAdapter().add(items);
    }

    @NonNull
    public TimesheetGroupModel remove(int index) {
        return getAdapter().remove(index);
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

    @Override
    public void finishLoading() {
        mLoading = false;
    }

    @Override
    public void refresh() {
        // Clear the items from the list and start loading from the beginning...
        getAdapter().clear();
        getPresenter().getTimesheet(getProjectId(), 0);
    }

    public void remove(List<TimeInAdapterResult> results) {
        getAdapter().remove(results);
    }

    public void update(List<TimeInAdapterResult> results) {
        getAdapter().set(results);
    }

    @Override
    public void onSelect() {
        if (null == mActionMode) {
            mActionMode = getActivity().startActionMode(mActionModeCallback);
        }
    }

    @Override
    public void onDeselect() {
        if (null == mActionMode) {
            return;
        }

        if (getAdapter().haveSelectedItems()) {
            return;
        }

        mActionMode.finish();
    }
}
