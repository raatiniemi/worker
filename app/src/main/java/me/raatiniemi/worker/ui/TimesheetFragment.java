package me.raatiniemi.worker.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.provider.ExpandableDataProvider.*;
import me.raatiniemi.worker.provider.TimesheetExpandableDataProvider;

public class TimesheetFragment extends Fragment
{
    private RecyclerView mRecyclerView;

    private TimesheetAdapter mTimesheetAdapter;

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

        TimesheetExpandableDataProvider provider = new TimesheetExpandableDataProvider(data);

        RecyclerViewExpandableItemManager manager = new RecyclerViewExpandableItemManager(savedInstanceState);

        mTimesheetAdapter = new TimesheetAdapter(provider);

        RecyclerView.Adapter wrapperAdapter = manager.createWrappedAdapter(mTimesheetAdapter);
        mRecyclerView.setAdapter(wrapperAdapter);

        manager.attachRecyclerView(mRecyclerView);
    }
}
