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

package me.raatiniemi.worker.presentation.projects.view;

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

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.ProjectResolverRepository;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.ClockIn;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.project.view.ProjectActivity;
import me.raatiniemi.worker.presentation.projects.model.ProjectsModel;
import me.raatiniemi.worker.presentation.projects.presenter.ProjectsPresenter;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;
import me.raatiniemi.worker.presentation.util.Settings;
import me.raatiniemi.worker.presentation.view.adapter.SimpleListAdapter;
import me.raatiniemi.worker.presentation.view.fragment.MvpFragment;

public class ProjectsFragment extends MvpFragment<ProjectsPresenter>
        implements ProjectsAdapter.OnProjectActionListener, SimpleListAdapter.OnItemClickListener, ProjectsView {
    private static final String FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at";

    /**
     * Tag for the new project fragment.
     */
    private static final String FRAGMENT_NEW_PROJECT_TAG = "new project";

    private static final String TAG = "ProjectsFragment";

    private RecyclerView recyclerView;

    private ProjectsAdapter adapter;

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

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(getAdapter());

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
        ProjectRepository projectRepository = new ProjectResolverRepository(
                getActivity().getContentResolver(),
                new ProjectCursorMapper(),
                new ProjectContentValuesMapper()
        );

        // Create the time repository.
        TimeRepository timeRepository = new TimeResolverRepository(
                getActivity().getContentResolver(),
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );

        return new ProjectsPresenter(
                getActivity(),
                EventBus.getDefault(),
                new GetProjects(projectRepository, timeRepository),
                new GetProjectTimeSince(timeRepository),
                new ClockActivityChange(
                        projectRepository,
                        timeRepository,
                        new ClockIn(timeRepository),
                        new ClockOut(timeRepository)
                ),
                new RemoveProject(projectRepository)
        );
    }

    @NonNull
    private ProjectsAdapter getAdapter() {
        if (null == adapter) {
            adapter = new ProjectsAdapter(getActivity(), this);
            adapter.setHintedImageButtonListener(
                    new HintedImageButtonListener(getActivity())
            );
            adapter.setOnItemClickListener(this);
        }

        return adapter;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<ProjectsModel> getProjects() {
        return getAdapter().getItems();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showGetProjectsErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_get_projects,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void addProjects(List<ProjectsModel> projects) {
        getAdapter().add(projects);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void addCreatedProject(@NonNull Project project) {
        ProjectsModel item = new ProjectsModel(project);
        int position = getAdapter().add(item);

        recyclerView.scrollToPosition(position);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showCreateProjectSuccessMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.message_project_created,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void updateProject(ProjectsModel project) {
        int position = getAdapter().findProject(project);
        if (RecyclerView.NO_POSITION == position) {
            Log.e(TAG, "Unable to find position for project in the adapter");
            return;
        }

        getAdapter().set(position, project);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showClockInErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_clock_in,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showClockOutErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_clock_out,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteProjectAtPosition(int position) {
        getAdapter().remove(position);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void restoreProjectAtPreviousPosition(
            int previousPosition,
            ProjectsModel project
    ) {
        getAdapter().add(previousPosition, project);

        recyclerView.scrollToPosition(previousPosition);
    }

    @Override
    public void showDeleteProjectSuccessMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.message_project_deleted,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showDeleteProjectErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_project_deleted,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void createNewProject() {
        NewProjectFragment newProject = NewProjectFragment.newFragment(new NewProjectFragment.OnCreateProjectListener() {
            @Override
            public void onCreateProject(Project project) {
                addCreatedProject(project);

                showCreateProjectSuccessMessage();
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

    /**
     * @inheritDoc
     */
    @Override
    public void reloadProjects() {
        getAdapter().clear();
        getPresenter().getProjects();
    }

    @Override
    public void onItemClick(@NonNull View view) {
        // Retrieve the position for the project from the RecyclerView.
        final int position = recyclerView.getChildAdapterPosition(view);
        if (RecyclerView.NO_POSITION == position) {
            Log.w(TAG, "Unable to retrieve project position for onItemClick");
            return;
        }

        try {
            // Retrieve the project from the retrieved position.
            final ProjectsModel item = getAdapter().get(position);
            final Project project = item.asProject();

            Intent intent = ProjectActivity.newIntent(
                    getActivity(),
                    project.getId()
            );
            startActivity(intent);
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "Unable to get project position", e);
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.error_message_unable_to_find_project,
                    Snackbar.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onClockActivityToggle(@NonNull final ProjectsModel project) {
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
                        @Override
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
    public void onClockActivityAt(@NonNull final ProjectsModel project) {
        ClockActivityAtFragment fragment = ClockActivityAtFragment.newInstance(project.asProject());
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
    public void onDelete(@NonNull final ProjectsModel project) {
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
