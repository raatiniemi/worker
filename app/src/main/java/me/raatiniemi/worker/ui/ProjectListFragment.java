package me.raatiniemi.worker.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;

public class ProjectListFragment extends Fragment
{
    public ProjectListFragment()
    {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_project_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_project_list);
        recyclerView.setLayoutManager(layoutManager);

        // Instantiate the data mapper for time and project.
        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();

        // Retrieve the available projects from the project data mapper.
        ArrayList<Project> projects = projectMapper.getProjects();

        ProjectListAdapter adapter = new ProjectListAdapter(projects);
        recyclerView.setAdapter(adapter);
    }
}
