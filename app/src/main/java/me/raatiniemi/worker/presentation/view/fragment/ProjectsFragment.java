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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.base.view.fragment.MvpFragment;
import me.raatiniemi.worker.presentation.base.view.adapter.SimpleListAdapter;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.ProjectRepository;
import me.raatiniemi.worker.data.repository.TimeRepository;
import me.raatiniemi.worker.data.repository.strategy.ProjectResolverStrategy;
import me.raatiniemi.worker.data.repository.strategy.TimeResolverStrategy;
import me.raatiniemi.worker.data.repository.strategy.TimeStrategy;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.ProjectProvider;
import me.raatiniemi.worker.presentation.presenter.TimesheetPresenter;
import me.raatiniemi.worker.presentation.view.activity.ProjectActivity;
import me.raatiniemi.worker.presentation.view.adapter.ProjectsAdapter;
import me.raatiniemi.worker.presentation.presenter.ProjectsPresenter;
import me.raatiniemi.worker.presentation.view.ProjectsView;
import me.raatiniemi.worker.util.HintedImageButtonListener;
import me.raatiniemi.worker.util.Settings;
import rx.Observable;

public class ProjectsFragment extends MvpFragment<ProjectsPresenter, List<Project>>
        implements ProjectsAdapter.OnProjectActionListener, SimpleListAdapter.OnItemClickListener, ProjectsView {
    public static final String MESSAGE_PROJECT_ID = "me.raatiniemi.activity.project.id";

    public static final String FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at";

    /**
     * Tag for the new project fragment.
     */
    public static final String FRAGMENT_NEW_PROJECT_TAG = "new project";

    private static final String TAG = "ProjectsFragment";

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
        mRecyclerView.setAdapter(getAdapter());

        getPresenter().attachView(this);
        getPresenter().getProjects();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Setup the subscription for refreshing active projects.
        getPresenter().beginRefreshingActiveProjects();

        // Initiate the refresh of active projects.
        getPresenter().refreshActiveProjects();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unsubscribe to the refreshing of active projects.
        getPresenter().stopRefreshingActiveProjects();
    }

    @Override
    protected ProjectsPresenter createPresenter() {
        // Create the project repository.
        ProjectRepository projectRepository = new ProjectResolverStrategy(
                getActivity().getContentResolver(),
                new ProjectCursorMapper()
        );

        // Create the time strategy/repository.
        TimeStrategy timeStrategy = new TimeResolverStrategy(
                getActivity().getContentResolver(),
                new TimeCursorMapper()
        );
        TimeRepository timeRepository = new TimeRepository(timeStrategy);

        return new ProjectsPresenter(
                getActivity(),
                new ProjectProvider(
                        getActivity(),
                        projectRepository,
                        timeRepository
                )
        );
    }

    @Override
    public List<Project> getData() {
        return getAdapter().getItems();
    }

    @Override
    public void setData(List<Project> data) {
        getAdapter().setItems(data);
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public ProjectsAdapter getAdapter() {
        if (null == mAdapter) {
            List<Project> items = new ArrayList<>();
            mAdapter = new ProjectsAdapter(getActivity(), items, this);
            mAdapter.setHintedImageButtonListener(
                    new HintedImageButtonListener(getActivity())
            );
            mAdapter.setOnItemClickListener(this);
        }

        return mAdapter;
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Project get(int index) {
        return getAdapter().get(index);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void set(int index, @NonNull Project item) {
        getAdapter().set(index, item);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(@NonNull Project item) {
        return getAdapter().add(item);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(@NonNull List<Project> items) {
        return getAdapter().add(items);
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Project remove(int index) {
        return getAdapter().remove(index);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void addCreatedProject(@NonNull Project project) {
        // Add the project to the adapter. Since this is a user action we
        // should always display the result, i.e. scroll down to the project.
        int position = add(project);
        mRecyclerView.scrollToPosition(position);

        // TODO: Add support for "undo", i.e. remove created project.
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.message_project_created,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void updateProject(Project project) {
        int position = getAdapter().findProject(project);
        if (0 > position) {
            Log.e(TAG, "Unable to find position for project in the adapter");
            return;
        }

        getAdapter().set(position, project);
    }

    @Override
    public void deleteProject(Project project) {
        int position = getAdapter().findProject(project);
        if (0 > position) {
            Log.w(TAG, "Unable to find position for project in the adapter");
            return;
        }

        getAdapter().remove(position);

        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.message_project_deleted,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void createNewProject() {
        NewProjectFragment newProject = new NewProjectFragment();
        newProject.setOnCreateProjectListener(new NewProjectFragment.OnCreateProjectListener() {
            @Override
            public Observable<Project> onCreateProject(Project project) {
                return getPresenter().createNewProject(project);
            }
        });
        newProject.show(getFragmentManager().beginTransaction(), FRAGMENT_NEW_PROJECT_TAG);
    }

    @Override
    public void refreshPositions(List<Integer> positions) {
        // Check that we have positions to refresh.
        if (positions.isEmpty()) {
            // We should never reach this code since there are supposed to be
            // checks for positions before the refreshPositions-method is called.
            Log.w(TAG, "No positions, skip refreshing projects");
            return;
        }

        // Iterate and refresh every position.
        Log.d(TAG, "Refreshing " + positions.size() + " projects");
        for (Integer position : positions) {
            getAdapter().notifyItemChanged(position);
        }
    }

    @Override
    public void onItemClick(@NonNull View view) {
        // Retrieve the position for the project from the RecyclerView.
        final int position = mRecyclerView.getChildAdapterPosition(view);
        if (RecyclerView.NO_POSITION == position) {
            Log.w(TAG, "Unable to retrieve project position for onItemClick");
            return;
        }

        try {
            // Retrieve the project from the retrieved position.
            final Project project = getAdapter().get(position);
            Intent intent = new Intent(getActivity(), ProjectActivity.class);
            intent.putExtra(ProjectsFragment.MESSAGE_PROJECT_ID, project.getId());

            startActivity(intent);
        } catch (IndexOutOfBoundsException e) {
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.error_message_unable_to_find_project,
                    Snackbar.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onClockActivityToggle(@NonNull final Project project) {
        if (project.isActive()) {
            // Check if clock out require confirmation.
            if (!Settings.shouldConfirmClockOut(getActivity())) {
                getPresenter().clockActivityChange(project, new Date());
                return;
            }

            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.confirm_clock_out_title))
                    .setMessage(getString(R.string.confirm_clock_out_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            getPresenter().clockActivityChange(project, new Date());
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return;
        }

        getPresenter().clockActivityChange(project, new Date());
    }

    @Override
    public void onClockActivityAt(@NonNull final Project project) {
        ClockActivityAtFragment fragment = ClockActivityAtFragment.newInstance(project);
        fragment.setOnClockActivityAtListener(new ClockActivityAtFragment.OnClockActivityAtListener() {
            @Override
            public void onClockActivityAt(Calendar calendar) {
                getPresenter().clockActivityChange(project, calendar.getTime());
            }
        });

        getFragmentManager().beginTransaction()
                .add(fragment, FRAGMENT_CLOCK_ACTIVITY_AT_TAG)
                .commit();
    }

    @Override
    public void onDelete(@NonNull final Project project) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_delete_project_title)
                .setMessage(R.string.confirm_delete_project_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPresenter().deleteProject(project);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
