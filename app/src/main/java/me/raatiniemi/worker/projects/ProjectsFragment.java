package me.raatiniemi.worker.projects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.ListAdapter;
import me.raatiniemi.worker.base.view.MvpFragment;
import me.raatiniemi.worker.model.project.Project;
import me.raatiniemi.worker.model.project.ProjectCollection;
import me.raatiniemi.worker.model.project.ProjectProvider;
import me.raatiniemi.worker.project.ProjectActivity;
import me.raatiniemi.worker.ui.NewProjectFragment;
import me.raatiniemi.worker.util.ClockActivityAtFragment;
import me.raatiniemi.worker.util.HintedImageButtonListener;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ProjectsFragment extends MvpFragment<ProjectsPresenter, ProjectCollection>
    implements ProjectsAdapter.OnClockActivityChangeListener, ListAdapter.OnItemClickListener, ProjectsView {
    public static final String MESSAGE_PROJECT_ID = "me.raatiniemi.activity.project.id";

    public static final String FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at";

    /**
     * Tag for the new project fragment.
     */
    public static final String FRAGMENT_NEW_PROJECT_TAG = "new project";

    private static final String TAG = "ProjectsFragment";

    private RecyclerView mRecyclerView;

    private ProjectsAdapter mAdapter;

    /**
     * Subscription for refreshing active projects.
     */
    private Subscription mRefreshActiveProjects;

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

        mAdapter = new ProjectsAdapter(getActivity(), this);
        mAdapter.setHintedImageButtonListener(new HintedImageButtonListener(getActivity()));
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        getPresenter().attachView(this);
        getPresenter().getProjects();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "Subscribe to refresh for active projects");
        mRefreshActiveProjects = Observable.interval(60, TimeUnit.SECONDS)
            .flatMap(new Func1<Long, Observable<List<Integer>>>() {
                @Override
                public Observable<List<Integer>> call(Long aLong) {
                    List<Integer> positions = new ArrayList<>();
                    ProjectCollection data = getData();
                    for (Project project : data) {
                        if (project.isActive()) {
                            Log.d(TAG, "Queuing refresh of project: " + project.getName());
                            positions.add(data.indexOf(project));
                        }
                    }
                    return Observable.just(positions);
                }
            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Integer>>() {
                @Override
                public void call(List<Integer> positions) {
                    if (positions.isEmpty()) {
                        Log.d(TAG, "No projects are active, nothing to refresh");
                        return;
                    }

                    for (Integer position : positions) {
                        mAdapter.notifyItemChanged(position);
                    }
                }
            });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (null != mRefreshActiveProjects && !mRefreshActiveProjects.isUnsubscribed()) {
            Log.d(TAG, "Unsubscribe to refresh for active projects");
            mRefreshActiveProjects.unsubscribe();
        }
        mRefreshActiveProjects = null;
    }

    @Override
    protected ProjectsPresenter createPresenter() {
        return new ProjectsPresenter(getActivity(), new ProjectProvider(getActivity()));
    }

    @Override
    public ProjectCollection getData() {
        return mAdapter.getItems();
    }

    @Override
    public void setData(ProjectCollection data) {
        mAdapter.setItems(data);
        mAdapter.notifyDataSetChanged();
    }

    public void addProject(Project project) {
        int position = mAdapter.add(project);
        mRecyclerView.scrollToPosition(position);

        String message = getString(R.string.fragment_new_project_create_successful);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        // TODO: Redesign the dismiss process.
        NewProjectFragment fragment = (NewProjectFragment) getFragmentManager().findFragmentByTag(FRAGMENT_NEW_PROJECT_TAG);
        if (null != fragment) {
            fragment.dismiss();
        }
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
            public void onCreateProject(Project project) {
                getPresenter().createNewProject(project);
            }
        });
        newProject.show(getFragmentManager().beginTransaction(), FRAGMENT_NEW_PROJECT_TAG);
    }

    void showCreateProjectError() {
        // Project name already exists, display error message to user.
        new AlertDialog.Builder(getActivity())
            .setTitle(getString(R.string.fragment_new_project_create_project_already_exists_title))
            .setMessage(getString(R.string.fragment_new_project_create_project_already_exists_description))
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing...
                }
            })
            .show();
    }

    void showClockActivityError() {
        new AlertDialog.Builder(getActivity())
            .setTitle(getString(R.string.projects_item_project_clock_out_before_clock_in_title))
            .setMessage(getString(R.string.projects_item_project_clock_out_before_clock_in_description))
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing...
                }
            })
            .show();
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
}
