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

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.interactor.GetTimesheet;
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime;
import me.raatiniemi.worker.domain.interactor.RemoveTime;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.base.view.fragment.MvpFragment;
import me.raatiniemi.worker.presentation.model.timesheet.TimesheetItem;
import me.raatiniemi.worker.presentation.presenter.TimesheetPresenter;
import me.raatiniemi.worker.presentation.view.TimesheetView;
import me.raatiniemi.worker.presentation.view.adapter.TimesheetAdapter;
import me.raatiniemi.worker.presentation.view.adapter.TimesheetAdapter.TimeInAdapterResult;

import static me.raatiniemi.worker.R.drawable.list_item_divider;

public class TimesheetFragment extends MvpFragment<TimesheetPresenter, List<TimesheetItem>>
        implements TimesheetAdapter.OnTimesheetListener, TimesheetView {
    private static final String TAG = "TimesheetFragment";

    private LinearLayoutManager mLinearLayoutManager;

    private TimesheetAdapter mAdapter;

    private TimeInAdapterResult mSelectedItem;

    private View mSelectedView;

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
                                    getPresenter().remove(mSelectedItem);

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
                    getPresenter().register(mSelectedItem);
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
            // If there's a selected row, we have
            // to clear the selection-state.
            if (null != mSelectedView) {
                // If the selected time is registered we have to restore the
                // activated state in case the user dismissed the action mode.
                Time time = mSelectedItem.getTime();
                mSelectedView.setActivated(time.isRegistered());

                mSelectedView.setSelected(false);
                mSelectedView = null;
            }

            mSelectedItem = null;
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
                new GetTimesheet(timeRepository),
                new MarkRegisteredTime(timeRepository),
                new RemoveTime(timeRepository)
        );
    }

    @Override
    public List<TimesheetItem> getData() {
        return getAdapter().getItems();
    }

    @Override
    public void setData(List<TimesheetItem> data) {
        getAdapter().setItems(data);
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public TimesheetAdapter getAdapter() {
        if (null == mAdapter) {
            List<TimesheetItem> items = new ArrayList<>();
            mAdapter = new TimesheetAdapter(items, this);
        }

        return mAdapter;
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public TimesheetItem get(int index) {
        return getAdapter().get(index);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void set(int index, @NonNull TimesheetItem item) {
        getAdapter().set(index, item);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(@NonNull TimesheetItem item) {
        return getAdapter().add(item);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(final int index, final @NonNull TimesheetItem item) {
        // TODO: Implement adding at specific index for TimesheetFragment.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(@NonNull List<TimesheetItem> items) {
        return getAdapter().add(items);
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public TimesheetItem remove(int index) {
        return getAdapter().remove(index);
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

    public void remove(TimeInAdapterResult result) {
        getAdapter().remove(result.getGroup(), result.getChild());
    }

    public void update(TimeInAdapterResult result) {
        getAdapter().set(result.getGroup(), result.getChild(), result.getTime());
    }

    @Override
    public boolean onTimeLongClick(View view, TimeInAdapterResult result) {
        boolean registered = false;

        // If we already have a selected item, we need to check the registered
        // status of the item. If it is registered we have to restore the
        // activated state for the view.
        if (null != mSelectedItem) {
            Time time = mSelectedItem.getTime();
            registered = time.isRegistered();
        }

        mSelectedItem = result;

        // If there's already a selected row, we have
        // to clear the selection-state.
        if (null != mSelectedView) {
            mSelectedView.setActivated(registered);
            mSelectedView.setSelected(false);
        }

        // Save the view for reference and put
        // the view in the selection-state.
        mSelectedView = view;
        mSelectedView.setSelected(true);

        // Regardless of whether the time for the view have been registered the
        // view should be deactivated. The selected background color should
        // take precedence of the activated.
        mSelectedView.setActivated(false);

        // Only start the ActionMode if none has already started.
        if (null == mActionMode) {
            mActionMode = getActivity().startActionMode(mActionModeCallback);
        }
        return true;
    }
}
