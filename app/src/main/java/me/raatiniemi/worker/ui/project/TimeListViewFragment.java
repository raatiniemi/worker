package me.raatiniemi.worker.ui.project;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.adapter.ProjectTimeListAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.ui.activity.ProjectListActivity;

public class TimeListViewFragment extends Fragment
{
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;

    private ProjectTimeListAdapter mAdapter;

    private long getProjectId()
    {
        return getArguments().getLong(ProjectListActivity.MESSAGE_PROJECT_ID, 0);
    }

    public TimeListViewFragment()
    {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_project_time_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.project_time_list_view);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Retrieve the project data from the mapper.
        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
        Project project = projectMapper.find(getProjectId());

        // Set the name for the activity.
        getActivity().setTitle(project.getName());

        mAdapter = new ProjectTimeListAdapter(project.getTime());
        mRecyclerView.setAdapter(mAdapter);
    }
}
