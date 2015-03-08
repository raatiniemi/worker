package me.raatiniemi.worker.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.util.ArrayList;
import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.TimeCollection;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;

public class ExpandableTimeListFragment extends Fragment
{
    private long getProjectId()
    {
        return getArguments().getLong(ProjectListFragment.MESSAGE_PROJECT_ID, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_expandable_time_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_expandable_time_list);
        recyclerView.setLayoutManager(layoutManager);

        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
        Project project = projectMapper.find(getProjectId());

        TimeMapper timeMapper = MapperRegistry.getTimeMapper();
        ArrayList<Pair<Date, TimeCollection>> data = timeMapper.findTime(project);

        RecyclerViewExpandableItemManager manager = new RecyclerViewExpandableItemManager(savedInstanceState);

        ExpandableTimeListAdapter adapter = new ExpandableTimeListAdapter(data);

        RecyclerView.Adapter wrapperAdapter = manager.createWrappedAdapter(adapter);
        recyclerView.setAdapter(wrapperAdapter);
    }
}
