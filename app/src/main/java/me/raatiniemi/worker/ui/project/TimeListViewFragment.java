package me.raatiniemi.worker.ui.project;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

    private ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            mode.getMenuInflater().inflate(R.menu.actions_project, menu);
            mode.setTitle("Actions");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            switch (item.getItemId()) {
                case R.id.actions_project_edit:
                    // TODO: Handle item edit.
                    mode.finish();
                    return true;
                case R.id.actions_project_delete:
                    // TODO: Handle item deletion.
                    mode.finish();
                    return true;
                default:
                    // TODO: Log unidentified action item clicked.
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            mActionMode = null;
        }
    };

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
        mAdapter.setEventListener(new ProjectTimeListAdapter.EventListener() {
            @Override
            public boolean onItemViewLongClick(View view) {
                if (null != mActionMode) {
                    return false;
                }

                mActionMode = getActivity().startActionMode(mActionModeCallback);
                // TODO: Set selection for the view.
                return true;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
