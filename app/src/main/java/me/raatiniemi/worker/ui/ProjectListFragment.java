package me.raatiniemi.worker.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.ui.fragment.ClockActivityAtFragment;

public class ProjectListFragment extends Fragment
{
    private RecyclerView mRecyclerView;

    private ProjectListAdapter mAdapter;

    private static final String FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at";

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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_project_list);
        mRecyclerView.setLayoutManager(layoutManager);

        // Instantiate the data mapper for time and project.
        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();

        // Retrieve the available projects from the project data mapper.
        ArrayList<Project> projects = projectMapper.getProjects();

        mAdapter = new ProjectListAdapter(projects);
        mAdapter.setOnClockActivityChangeListener(new ProjectListAdapter.OnClockActivityChangeListener() {
            @Override
            public void onClockActivityToggle(View view) {
                int position = mRecyclerView.getChildPosition(view);
                if (RecyclerView.NO_POSITION < position) {
                    Project project = mAdapter.get(position);
                    if (null != project) {
                        onClockActivityChange(position, project, new Date());
                    }
                }
            }

            @Override
            public void onClockActivityAt(View view) {
                int position = mRecyclerView.getChildPosition(view);
                if (RecyclerView.NO_POSITION < position) {
                    Project project = mAdapter.get(position);
                    if (null != project) {
                        final ClockActivityAtFragment fragment = ClockActivityAtFragment.newInstance(project, position);
                        fragment.setOnClockActivityAtListener(new ClockActivityAtFragment.OnClockActivityAtListener() {
                            @Override
                            public void onClockActivityAt(Project project, Calendar calendar, int index)
                            {
                                onClockActivityChange(index, project, calendar.getTime());
                            }
                        });

                        getFragmentManager().beginTransaction()
                            .add(fragment, FRAGMENT_CLOCK_ACTIVITY_AT_TAG)
                            .commit();
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void onClockActivityChange(int position, Project project, Date date)
    {
        try {
            // Retrieve the project and time data mappers.
            ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
            TimeMapper timeMapper = MapperRegistry.getTimeMapper();

            Time time;

            // Depending on whether the project is active,
            // we're going to clock out or clock in.
            if (project.isActive()) {
                time = project.clockOutAt(date);
                timeMapper.update(time);
            } else {
                time = project.clockInAt(date);
                timeMapper.insert(time);
            }

            // Retrieve the updated project from the data mapper.
            project = projectMapper.find(project.getId());
            mAdapter.set(position, project);
        } catch (DomainException e) {
            new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.project_list_item_project_clock_out_before_clock_in_title))
                .setMessage(getString(R.string.project_list_item_project_clock_out_before_clock_in_description))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing...
                    }
                })
                .show();
        }
    }
}
