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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.service.ongoing.ProjectNotificationService;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.project.view.ProjectActivity;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.presenter.ProjectsPresenter;
import me.raatiniemi.worker.presentation.util.ConfirmClockOutPreferences;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;
import me.raatiniemi.worker.presentation.view.adapter.SimpleListAdapter;
import me.raatiniemi.worker.presentation.view.fragment.MvpFragment;
import timber.log.Timber;

import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.util.NullUtil.nonNull;

public class ProjectsFragment extends MvpFragment<ProjectsPresenter>
        implements OnProjectActionListener, SimpleListAdapter.OnItemClickListener, ProjectsView {
    private static final String FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at";

    /**
     * Tag for the new project fragment.
     */
    private static final String FRAGMENT_NEW_PROJECT_TAG = "new project";

    @Inject
    ConfirmClockOutPreferences confirmClockOutPreferences;

    @Inject
    ProjectsPresenter presenter;

    private RecyclerView recyclerView;

    private ProjectsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = ButterKnife.findById(view, R.id.fragment_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(getAdapter());

        ((Worker) getActivity().getApplication()).getProjectsComponent()
                .inject(this);

        presenter.attachView(this);
        presenter.getProjects();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (nonNull(presenter)) {
            presenter.detachView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Setup the subscription for refreshing active projects.
        presenter.beginRefreshingActiveProjects();

        // Initiate the refresh of active projects.
        presenter.refreshActiveProjects();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unsubscribe to the refreshing of active projects.
        presenter.stopRefreshingActiveProjects();
    }

    @NonNull
    private ProjectsAdapter getAdapter() {
        if (isNull(adapter)) {
            adapter = new ProjectsAdapter(getResources(), this, new HintedImageButtonListener(getActivity()));
            adapter.setOnItemClickListener(this);
        }

        return adapter;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<ProjectsItem> getProjects() {
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
    public void addProjects(List<ProjectsItem> projects) {
        getAdapter().add(projects);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void addCreatedProject(@NonNull Project project) {
        ProjectsItem item = new ProjectsItem(project);
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
    public void updateNotificationForProject(ProjectsItem project) {
        ProjectNotificationService.startServiceWithContext(
                getActivity(),
                project.asProject()
        );
    }

    @Override
    public void updateProject(ProjectsItem project) {
        int position = getAdapter().findProject(project);
        if (RecyclerView.NO_POSITION == position) {
            Timber.e("Unable to find position for project in the adapter");
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
            ProjectsItem project
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
        NewProjectFragment newProject = NewProjectFragment.newInstance(project -> {
            addCreatedProject(project);

            showCreateProjectSuccessMessage();
        });

        getFragmentManager().beginTransaction()
                .add(newProject, FRAGMENT_NEW_PROJECT_TAG)
                .commit();
    }

    @Override
    public void refreshPositions(List<Integer> positions) {
        // Check that we have positions to refresh.
        if (positions.isEmpty()) {
            // We should never reach this code since there are supposed to be
            // checks for positions before the refreshPositions-method is called.
            Timber.w("No positions, skip refreshing projects");
            return;
        }

        // Iterate and refresh every position.
        Timber.d("Refreshing %d projects", positions.size());
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
        presenter.getProjects();
    }

    @Override
    public void onItemClick(@NonNull View view) {
        // Retrieve the position for the project from the RecyclerView.
        final int position = recyclerView.getChildAdapterPosition(view);
        if (RecyclerView.NO_POSITION == position) {
            Timber.w("Unable to retrieve project position for onItemClick");
            return;
        }

        try {
            // Retrieve the project from the retrieved position.
            final ProjectsItem item = getAdapter().get(position);
            final Project project = item.asProject();

            Intent intent = ProjectActivity.newIntent(
                    getActivity(),
                    project.getId()
            );
            startActivity(intent);
        } catch (IndexOutOfBoundsException e) {
            Timber.w(e, "Unable to get project position");
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.error_message_unable_to_find_project,
                    Snackbar.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onClockActivityToggle(@NonNull final ProjectsItem project) {
        if (project.isActive()) {
            // Check if clock out require confirmation.
            if (!confirmClockOutPreferences.shouldConfirmClockOut()) {
                presenter.clockActivityChange(project, new Date());
                return;
            }

            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.confirm_clock_out_title))
                    .setMessage(getString(R.string.confirm_clock_out_message))
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> presenter.clockActivityChange(project, new Date()))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return;
        }

        presenter.clockActivityChange(project, new Date());
    }

    @Override
    public void onClockActivityAt(@NonNull final ProjectsItem project) {
        ClockActivityAtFragment fragment = ClockActivityAtFragment.newInstance(
                project.asProject(),
                calendar -> presenter.clockActivityChange(project, calendar.getTime())
        );

        getFragmentManager().beginTransaction()
                .add(fragment, FRAGMENT_CLOCK_ACTIVITY_AT_TAG)
                .commit();
    }

    @Override
    public void onDelete(@NonNull final ProjectsItem project) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_delete_project_title)
                .setMessage(R.string.confirm_delete_project_message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> presenter.deleteProject(project))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
