package me.raatiniemi.worker.projects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.ListAdapter;
import me.raatiniemi.worker.base.view.MvpFragment;
import me.raatiniemi.worker.model.domain.project.Project;
import me.raatiniemi.worker.model.domain.project.ProjectProvider;
import me.raatiniemi.worker.project.ProjectActivity;
import me.raatiniemi.worker.ui.NewProjectFragment;
import me.raatiniemi.worker.util.ClockActivityAtFragment;
import me.raatiniemi.worker.util.HintedImageButtonListener;
import rx.Observable;

public class ProjectsFragment extends MvpFragment<ProjectsPresenter, List<Project>>
    implements ProjectsAdapter.OnClockActivityChangeListener, ProjectsAdapter.OnProjectDeleteListener, ListAdapter.OnItemClickListener, ProjectsView {
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

        mAdapter = new ProjectsAdapter(getActivity(), this, this);
        mAdapter.setHintedImageButtonListener(new HintedImageButtonListener(getActivity()));
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

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
        return new ProjectsPresenter(getActivity(), new ProjectProvider(getActivity()));
    }

    @Override
    public List<Project> getData() {
        return mAdapter.getItems();
    }

    @Override
    public void setData(List<Project> data) {
        mAdapter.setItems(data);
    }

    @Override
    public void addProject(Project project) {
        int position = mAdapter.add(project);
        mRecyclerView.scrollToPosition(position);

        // TODO: Add support for "undo", i.e. remove created project.
        Snackbar.make(
            getActivity().findViewById(android.R.id.content),
            R.string.fragment_new_project_create_successful,
            Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void updateProject(Project project) {
        int position = mAdapter.findProject(project);
        if (0 > position) {
            Log.e(TAG, "Unable to find position for project in the adapter");
            return;
        }

        mAdapter.set(position, project);
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
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onItemClick(View view) {
        // Retrieve the position for the project from the RecyclerView.
        final int position = mRecyclerView.getChildPosition(view);
        if (RecyclerView.NO_POSITION == position) {
            Log.w(TAG, "Unable to retrieve project position for onItemClick");
            return;
        }

        // Retrieve the project from the retrieved position.
        final Project project = mAdapter.get(position);
        if (null == project) {
            Log.w(TAG, "Unable to retrieve project from position " + position);
            return;
        }

        Intent intent = new Intent(getActivity(), ProjectActivity.class);
        intent.putExtra(ProjectsFragment.MESSAGE_PROJECT_ID, project.getId());

        startActivity(intent);
    }

    @Override
    public void onClockActivityToggle(final Project project) {
        // TODO: Add configuration for disabling confirm dialog.
        // When using the toggle clock activity functionality, the user
        // have to confirm the clock out.
        if (project.isActive()) {
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
    public void onClockActivityAt(final Project project) {
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
    public void onProjectDelete(Project project) {
        getPresenter().deleteProject(project);
    }
}
