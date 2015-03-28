package me.raatiniemi.worker.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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

import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.provider.ExpandableDataProvider.*;
import me.raatiniemi.worker.provider.TimesheetExpandableDataProvider;
import me.raatiniemi.worker.provider.TimesheetExpandableDataProvider.*;

public class TimesheetFragment extends Fragment
{
    private static final String TAG = "TimesheetFragment";

    private Project mProject;

    private LinearLayoutManager mLinearLayoutManager;

    private RecyclerView mRecyclerView;

    private TimesheetExpandableDataProvider mProvider;

    private TimesheetAdapter mTimesheetAdapter;

    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;

    private long mExpandablePosition = -1;

    private View mSelectedView;

    private ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
        {
            // TODO: Retrieve title from resources, for localization support.
            actionMode.setTitle("Actions");
            actionMode.getMenuInflater().inflate(R.menu.actions_project, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem item)
        {
            boolean finish = false;

            long expandablePosition = mExpandablePosition;
            switch (item.getItemId()) {
                case R.id.actions_project_delete:
                    remove(expandablePosition);
                    finish = true;
                    break;
                default:
                    Log.w(TAG, "Undefined action: " + item.getItemId());
                    break;
            }

            mExpandablePosition = -1;
            if (finish) {
                actionMode.finish();
            }
            return finish;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode)
        {
            // If there's a selected row, we have
            // to clear the selection-state.
            if (null != mSelectedView) {
                mSelectedView.setSelected(false);
                mSelectedView = null;
            }

            mActionMode = null;
        }
    };

    private boolean mLoading = false;

    private long getProjectId()
    {
        return getArguments().getLong(ProjectListFragment.MESSAGE_PROJECT_ID, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_timesheet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
        mProject = projectMapper.find(getProjectId());

        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_timesheet);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(false);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
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
                        int offset = mTimesheetAdapter.getGroupCount();

                        TimeMapper timeMapper = MapperRegistry.getTimeMapper();
                        List<Groupable> data = timeMapper.findIntervalByProject(mProject, offset);

                        // Check if we retrieved any additional items.
                        //
                        // If we didn't, we're at the end of the available data,
                        // there's no reason to unlock the loading (i.e. mLoading
                        // should continue to be true).
                        if (0 < data.size()) {
                            for (Groupable groupable : data) {
                                mTimesheetAdapter.addGroup(groupable);
                            }
                            mLoading = false;
                        }
                    }
                }
            }
        });

        TimeMapper timeMapper = MapperRegistry.getTimeMapper();
        List<Groupable> data = timeMapper.findIntervalByProject(mProject);

        mProvider = new TimesheetExpandableDataProvider(data);

        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(savedInstanceState);

        mTimesheetAdapter = new TimesheetAdapter(mProvider);
        mTimesheetAdapter.setOnTimesheetListener(new TimesheetAdapter.OnTimesheetListener() {
            @Override
            public boolean onTimeLongClick(View view)
            {
                // The position of the item within the recycler view is referred to as the flat
                // position. With this position we have to retrieve the expandable position, which
                // basically is the group and child within bit shifted long.
                //
                // From this position we can later on get the actual group and child position.
                int flatPosition = mRecyclerView.getChildPosition(view);
                if (RecyclerView.NO_POSITION < flatPosition) {
                    mExpandablePosition = mRecyclerViewExpandableItemManager.getExpandablePosition(flatPosition);

                    // If there's already a selected row, we have
                    // to clear the selection-state.
                    if (null != mSelectedView) {
                        mSelectedView.setSelected(false);
                    }

                    // Save the view for reference and put
                    // the view in the selection-state.
                    mSelectedView = view;
                    mSelectedView.setSelected(true);

                    // Only start the ActionMode if none has already started.
                    if (null == mActionMode) {
                        mActionMode = getActivity().startActionMode(mActionModeCallback);
                    }
                    return true;
                }
                return false;
            }
        });

        RecyclerView.Adapter wrapperAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(mTimesheetAdapter);
        mRecyclerView.setAdapter(wrapperAdapter);
        mRecyclerView.addItemDecoration(
            new SimpleListDividerDecorator(
                getResources().getDrawable(R.drawable.expandable_list_item_divider),
                true
            )
        );

        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);
    }

    private void remove(long expandablePosition)
    {
        int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        // TODO: Migrate the mapper remove call to the provider?
        TimeChild child = (TimeChild) mProvider.getChildItem(groupPosition, childPosition);
        Time time = child.getTime();

        TimeMapper timeMapper = MapperRegistry.getTimeMapper();
        if (timeMapper.remove(time)) {
            Log.d(TAG, "Removing item: "+ groupPosition +":"+ childPosition);
            mTimesheetAdapter.remove(groupPosition, childPosition);

            // Create the intent for the broadcast to update the
            // project list. We have to supply the project id,
            // otherwise we're unable to properly update the view.
            //
            // TODO: Properly name the broadcast intent.
            // The name of the broadcast intent should be supplied
            // by the fragment we're trying to update, in case we'd
            // want to update the fragment from an additional location.
            Intent intent = new Intent("project-list-view-update");
            intent.putExtra("project_id", getProjectId());

            // Send the project update broadcast.
            LocalBroadcastManager.getInstance(getActivity())
                .sendBroadcast(intent);
        }
    }
}
