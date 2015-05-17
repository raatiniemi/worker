package me.raatiniemi.worker.projects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.ListAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.base.view.MvpFragment;
import me.raatiniemi.worker.ui.MainActivity;
import me.raatiniemi.worker.ui.TimesheetFragment;
import me.raatiniemi.worker.util.ClockActivityAtFragment;
import me.raatiniemi.worker.util.DateIntervalFormat;
import me.raatiniemi.worker.util.HintedImageButtonListener;
import me.raatiniemi.worker.util.ProjectCollection;

public class ProjectsFragment extends MvpFragment<ProjectsPresenter> implements ProjectsView {
    public static final String MESSAGE_PROJECT_ID = "me.raatiniemi.activity.project.id";

    private static final String TAG = "ProjectsFragment";

    private static final String FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at";

    private RecyclerView mRecyclerView;

    private ProjectsAdapter mAdapter;

    public ProjectsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_projects);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new ProjectsAdapter(getActivity(), new DateIntervalFormat());
        mAdapter.setHintedImageButtonListener(new HintedImageButtonListener(getActivity()));
        mAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = mRecyclerView.getChildPosition(view);
                if (RecyclerView.NO_POSITION < position) {
                    Project project = mAdapter.get(position);
                    if (null != project) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(MESSAGE_PROJECT_ID, project.getId());

                        TimesheetFragment fragment = new TimesheetFragment();
                        fragment.setArguments(bundle);

                        getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment, MainActivity.FRAGMENT_TIMESHEET_TAG)
                            .addToBackStack(MainActivity.FRAGMENT_PROJECT_LIST_TAG)
                            .commit();
                    }
                }
            }
        });
        mAdapter.setOnClockActivityChangeListener(new ProjectsAdapter.OnClockActivityChangeListener() {
            @Override
            public void onClockActivityToggle(View view) {
                final int position = mRecyclerView.getChildPosition(view);
                if (RecyclerView.NO_POSITION < position) {
                    Project project = mAdapter.get(position);
                    if (null != project) {
                        if (project.isActive()) {
                            // TODO: Add configuration for disabling confirm dialog.
                            // When using the toggle clock activity functionality, the user
                            // have to confirm the clock out.
                            new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.confirm_clock_out_title))
                                .setMessage(getString(R.string.confirm_clock_out_message))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        onClockActivityChange(position, new Date());
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                        } else {
                            onClockActivityChange(position, new Date());
                        }
                    }
                }
            }

            @Override
            public void onClockActivityAt(View view) {
                int position = mRecyclerView.getChildPosition(view);
                if (RecyclerView.NO_POSITION < position) {
                    ClockActivityAtFragment fragment = ClockActivityAtFragment.newInstance(position);
                    fragment.setOnClockActivityAtListener(new ClockActivityAtFragment.OnClockActivityAtListener() {
                        @Override
                        public void onClockActivityAt(int position, Calendar calendar) {
                            onClockActivityChange(position, calendar.getTime());
                        }
                    });

                    getFragmentManager().beginTransaction()
                        .add(fragment, FRAGMENT_CLOCK_ACTIVITY_AT_TAG)
                        .commit();
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        getPresenter().attachView(this);
        getPresenter().loadProjects(MapperRegistry.getProjectMapper());
    }

    @Override
    protected ProjectsPresenter createPresenter() {
        return new ProjectsPresenter();
    }

    public void setData(ProjectCollection projects) {
        mAdapter.setItems(projects);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addProject(Project project) {
        // Add the project to the adapter, and scroll down
        // to the new project.
        int position = mAdapter.add(project);
        mRecyclerView.scrollToPosition(position);
    }

    private void onClockActivityChange(int position, Date date) {
        try {
            // Retrieve the project and time data mappers.
            ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
            TimeMapper timeMapper = MapperRegistry.getTimeMapper();

            Project project = mAdapter.get(position);
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
            project = projectMapper.reload(project.getId());
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
