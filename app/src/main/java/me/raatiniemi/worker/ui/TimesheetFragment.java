package me.raatiniemi.worker.ui;

import android.app.Fragment;
import android.os.Bundle;
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

    private RecyclerView mRecyclerView;

    private TimesheetExpandableDataProvider mProvider;

    private TimesheetAdapter mTimesheetAdapter;

    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;

    private int mSelectedPosition = -1;

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

            int position = mSelectedPosition;
            switch (item.getItemId()) {
                case R.id.actions_project_delete:
                    remove(position);
                    finish = true;
                    break;
                default:
                    // TODO: Log unidentified action item clicked.
            }


            mSelectedPosition = -1;
            if (finish) {
                actionMode.finish();
            }
            return finish;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode)
        {
            mActionMode = null;
        }
    };

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_timesheet);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);

        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
        Project project = projectMapper.find(getProjectId());

        TimeMapper timeMapper = MapperRegistry.getTimeMapper();
        List<Groupable> data = timeMapper.findTime(project);

        mProvider = new TimesheetExpandableDataProvider(data);

        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(savedInstanceState);

        mTimesheetAdapter = new TimesheetAdapter(mProvider);
        mTimesheetAdapter.setOnTimesheetListener(new TimesheetAdapter.OnTimesheetListener() {
            @Override
            public boolean onTimeLongClick(View view)
            {
                mSelectedPosition = mRecyclerView.getChildPosition(view);
                if (RecyclerView.NO_POSITION < mSelectedPosition) {
                    // TODO: Set selection for the view.

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

        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);
    }

    private void remove(int position)
    {
        // The incoming "position" contains the position of the row refereed to as the
        // flat position, i.e. position of the row in the recycler views current state
        // (with expanded groups, etc.).
        //
        // From this we need to get the expanded position to be able to retrieve the
        // actual group and child of which we'd like to remove.
        long expandablePosition = mRecyclerViewExpandableItemManager.getExpandablePosition(position);
        int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        // TODO: Migrate the mapper remove call to the provider?
        TimeChild child = (TimeChild) mProvider.getChildItem(groupPosition, childPosition);
        Time time = child.getTime();

        TimeMapper timeMapper = MapperRegistry.getTimeMapper();
        if (timeMapper.remove(time)) {
            Log.d(TAG, "Removing item: "+ groupPosition +":"+ childPosition);
            mTimesheetAdapter.remove(position, groupPosition, childPosition);
        }
    }
}
