/*
 * Copyright (C) 2017 Worker Project
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.service.ongoing.ProjectNotificationService;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.project.view.ProjectActivity;
import me.raatiniemi.worker.presentation.projects.model.CreateProjectEvent;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItemAdapterResult;
import me.raatiniemi.worker.presentation.projects.viewmodel.ClockActivityViewModel;
import me.raatiniemi.worker.presentation.projects.viewmodel.ProjectsViewModel;
import me.raatiniemi.worker.presentation.projects.viewmodel.RefreshActiveProjectsViewModel;
import me.raatiniemi.worker.presentation.projects.viewmodel.RemoveProjectViewModel;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.util.ConfirmClockOutPreferences;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;
import me.raatiniemi.worker.presentation.view.adapter.SimpleListAdapter;
import me.raatiniemi.worker.presentation.view.dialog.RxDialog;
import me.raatiniemi.worker.presentation.view.fragment.RxFragment;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static me.raatiniemi.worker.presentation.util.RxUtil.applySchedulers;
import static me.raatiniemi.worker.presentation.util.RxUtil.unsubscribeIfNotNull;

public class ProjectsFragment extends RxFragment
        implements OnProjectActionListener, SimpleListAdapter.OnItemClickListener {
    private static final String FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at";
    @Inject
    ProjectsViewModel.ViewModel projectsViewModel;
    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @Inject
    RefreshActiveProjectsViewModel.ViewModel refreshViewModel;
    @Inject
    ClockActivityViewModel.ViewModel clockActivityViewModel;
    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @Inject
    RemoveProjectViewModel.ViewModel removeProjectViewModel;

    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @Inject
    EventBus eventBus;

    @Inject
    TimeSummaryPreferences timeSummaryPreferences;

    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @Inject
    ConfirmClockOutPreferences confirmClockOutPreferences;

    private Subscription refreshProjectsSubscription;
    private RecyclerView recyclerView;

    ProjectsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Worker) getActivity().getApplication())
                .getProjectsComponent()
                .inject(this);

        eventBus.register(this);

        adapter = new ProjectsAdapter(getResources(), this, new HintedImageButtonListener(getActivity()));
        adapter.setOnItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = ButterKnife.findById(view, R.id.fragment_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        int startingPointForTimeSummary = timeSummaryPreferences.getStartingPointForTimeSummary();
        projectsViewModel.input.startingPointForTimeSummary(startingPointForTimeSummary);
        clockActivityViewModel.input.startingPointForTimeSummary(startingPointForTimeSummary);

        projectsViewModel.output.projects()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(adapter::add);

        projectsViewModel.error.projectsError()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(__ -> showGetProjectsErrorMessage());

        clockActivityViewModel.output.clockInSuccess()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(result -> {
                    updateNotificationForProject(result.getProjectsItem());
                    updateProject(result);
                });

        clockActivityViewModel.error.clockInError()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(__ -> showClockInErrorMessage());

        clockActivityViewModel.output.clockOutSuccess()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(result -> {
                    updateNotificationForProject(result.getProjectsItem());
                    updateProject(result);
                });

        clockActivityViewModel.error.clockOutError()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(__ -> showClockOutErrorMessage());

        removeProjectViewModel.output.removeProjectSuccess()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(__ -> showDeleteProjectSuccessMessage());

        removeProjectViewModel.error.removeProjectError()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    restoreProjectAtPreviousPosition(result);
                    showDeleteProjectErrorMessage();
                });

        refreshViewModel.output.positionsForActiveProjects()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(this::refreshPositions);
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshProjectsSubscription = Observable.interval(60, TimeUnit.SECONDS, Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> refreshViewModel.input.projects(adapter.getItems()));

        refreshViewModel.input.projects(adapter.getItems());
    }

    @Override
    public void onPause() {
        super.onPause();

        unsubscribeIfNotNull(refreshProjectsSubscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        eventBus.unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CreateProjectEvent event) {
        addCreatedProject(event.getProject());
    }

    private void addCreatedProject(@NonNull Project project) {
        ProjectsItem item = new ProjectsItem(project);
        int position = adapter.add(item);

        recyclerView.scrollToPosition(position);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OngoingNotificationActionEvent __) {
        reloadProjects();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TimeSummaryStartingPointChangeEvent __) {
        int startingPointForTimeSummary = timeSummaryPreferences.getStartingPointForTimeSummary();
        projectsViewModel.input.startingPointForTimeSummary(startingPointForTimeSummary);
        clockActivityViewModel.input.startingPointForTimeSummary(startingPointForTimeSummary);

        reloadProjects();
    }

    private void showGetProjectsErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_get_projects,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void reloadProjects() {
        adapter.clear();

        // TODO: Move to input event for view model.
        projectsViewModel.output.projects()
                .compose(bindToLifecycle())
                .subscribe(adapter::add);
    }

    private void refreshPositions(List<Integer> positions) {
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
            adapter.notifyItemChanged(position);
        }
    }

    private void updateNotificationForProject(ProjectsItem project) {
        ProjectNotificationService.startServiceWithContext(
                getActivity(),
                project.asProject()
        );
    }

    private void updateProject(ProjectsItemAdapterResult result) {
        adapter.set(result.getPosition(), result.getProjectsItem());
    }

    private void showClockInErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_clock_in,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void showClockOutErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_clock_out,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void deleteProjectAtPosition(int position) {
        adapter.remove(position);
    }

    private void restoreProjectAtPreviousPosition(ProjectsItemAdapterResult result) {
        adapter.add(result.getPosition(), result.getProjectsItem());

        recyclerView.scrollToPosition(result.getPosition());
    }

    private void showDeleteProjectSuccessMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.message_project_deleted,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void showDeleteProjectErrorMessage() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_project_deleted,
                Snackbar.LENGTH_SHORT
        ).show();
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
            final ProjectsItem item = adapter.get(position);
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
    public void onClockActivityToggle(@NonNull final ProjectsItemAdapterResult result) {
        final ProjectsItem projectsItem = result.getProjectsItem();
        if (projectsItem.isActive()) {
            // Check if clock out require confirmation.
            if (!confirmClockOutPreferences.shouldConfirmClockOut()) {
                clockActivityViewModel.input.clockOut(result, new Date());
                return;
            }

            ConfirmClockOutDialog.show(getActivity())
                    .filter(RxDialog::isPositive)
                    .subscribe(
                            __ -> clockActivityViewModel.input.clockOut(result, new Date()),
                            Timber::w
                    );
            return;
        }

        clockActivityViewModel.input.clockIn(result, new Date());
    }

    @Override
    public void onClockActivityAt(@NonNull final ProjectsItemAdapterResult result) {
        final ProjectsItem projectsItem = result.getProjectsItem();
        ClockActivityAtFragment fragment = ClockActivityAtFragment.newInstance(
                projectsItem.asProject(),
                calendar -> {
                    if (projectsItem.isActive()) {
                        clockActivityViewModel.input.clockOut(result, calendar.getTime());
                        return;
                    }

                    clockActivityViewModel.input.clockIn(result, calendar.getTime());
                }
        );

        getFragmentManager().beginTransaction()
                .add(fragment, FRAGMENT_CLOCK_ACTIVITY_AT_TAG)
                .commit();
    }

    @Override
    public void onDelete(@NonNull final ProjectsItemAdapterResult result) {
        RemoveProjectDialog.show(getActivity())
                .filter(RxDialog::isPositive)
                .subscribe(
                        __ -> {
                            deleteProjectAtPosition(result.getPosition());

                            removeProjectViewModel.input.remove(result);
                        },
                        Timber::w
                );
    }
}
