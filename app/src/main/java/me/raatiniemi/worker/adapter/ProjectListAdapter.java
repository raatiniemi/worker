package me.raatiniemi.worker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatButton;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.application.Worker;
import me.raatiniemi.worker.domain.Project;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> implements View.OnClickListener
{
    public interface OnProjectActivityChangeListener
    {
        public void onProjectActivityToggle(Project project, int index);
    }

    private OnProjectActivityChangeListener mActivityCallback;
    private ArrayList<Project> mProjects;

    public ProjectListAdapter(OnProjectActivityChangeListener activityCallback, ArrayList<Project> projects)
    {
        mActivityCallback = activityCallback;
        mProjects = projects;
    }

    @Override
    public int getItemCount()
    {
        return mProjects.size();
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder projectViewHolder, int index)
    {
        Project project = mProjects.get(index);

        projectViewHolder.mName.setText(project.getName());
        projectViewHolder.mTime.setText(project.summarizeTime());
        projectViewHolder.mDescription.setText(project.getDescription());

        // If the project description is empty
        // the view should be hidden.
        int visibility = View.VISIBLE;
        if (projectViewHolder.mDescription.getText().length() == 0) {
            visibility = View.INVISIBLE;
        }
        projectViewHolder.mDescription.setVisibility(visibility);

        // Set row index and the click listener.
        projectViewHolder.mClockActivity.setTag(index);
        projectViewHolder.mClockActivity.setOnClickListener(this);

        // Depending on whether the project is active the text
        // for the clock activity view should be altered, and
        // visibility for the clocked in view.
        int clockActivityId = R.string.project_list_item_project_clock_in;
        int clockedInVisibility = View.INVISIBLE;
        if (project.isActive()) {
            clockActivityId = R.string.project_list_item_project_clock_out;
            clockedInVisibility = View.VISIBLE;

            // TODO: Modify the clocked in timestamp.
        }

        // Retrieve the string from the resources and update the view.
        String clockActivity = Worker.getContext().getResources().getString(clockActivityId);
        projectViewHolder.mClockActivity.setText(clockActivity);

        // Set the visibility for the clocked in view.
        projectViewHolder.mClockedIn.setVisibility(clockedInVisibility);
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int index)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View projectView = inflater.inflate(R.layout.project_list_item, viewGroup, false);

        return new ProjectViewHolder(projectView);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.project_clock_activity:
                // Retrieve the index from the view and
                // get the project based on the index.
                int index = (int) view.getTag();
                Project project = mProjects.get(index);

                // Toggle the project activity.
                mActivityCallback.onProjectActivityToggle(project, index);
                break;
        }
    }

    public void addProject(Project project)
    {
        // Retrieve the number of elements before adding the project,
        // hence getting the index of the new project.
        int index = mProjects.size();
        mProjects.add(project);

        // Notify the adapter that the project have been inserted.
        notifyItemInserted(index);
    }

    public void updateProject(Project project, int index)
    {
        mProjects.set(index, project);

        notifyItemChanged(index);
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView mName;
        protected TextView mTime;
        protected TextView mDescription;
        protected FlatButton mClockActivity;
        protected RelativeLayout mClockedIn;

        public ProjectViewHolder(View view)
        {
            super(view);

            mName = (TextView) view.findViewById(R.id.project_name);
            mTime = (TextView) view.findViewById(R.id.project_time);
            mDescription = (TextView) view.findViewById(R.id.project_description);
            mClockActivity = (FlatButton) view.findViewById(R.id.project_clock_activity);
            mClockedIn = (RelativeLayout) view.findViewById(R.id.project_clocked_in);
        }
    }
}
