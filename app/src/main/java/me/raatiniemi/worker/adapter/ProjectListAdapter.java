package me.raatiniemi.worker.adapter;

import android.content.res.Resources;
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
    public interface OnProjectListListener
    {
        public void onProjectActivityToggle(Project project, int index);

        public void onProjectClockOutAt(Project project, int index);

        public void onProjectOpen(Project project);
    }

    private OnProjectListListener mActivityCallback;
    private ArrayList<Project> mProjects;

    public ProjectListAdapter(OnProjectListListener activityCallback, ArrayList<Project> projects)
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

        // Add the on click listener for the card view.
        // Will open the single project activity.
        projectViewHolder.itemView.setOnClickListener(this);
        projectViewHolder.itemView.setTag(index);

        projectViewHolder.getName().setText(project.getName());
        projectViewHolder.getTime().setText(project.summarizeTime());
        projectViewHolder.getDescription().setText(project.getDescription());

        // If the project description is empty the view should be hidden.
        int visibility = View.VISIBLE;
        if (project.getDescription() == null || project.getDescription().isEmpty()) {
            visibility = View.GONE;
        }
        projectViewHolder.getDescription().setVisibility(visibility);

        // Set row index and the click listener.
        projectViewHolder.getClockActivity().setTag(index);
        projectViewHolder.getClockActivity().setOnClickListener(this);

        // Depending on whether the project is active the text
        // for the clock activity view should be altered, and
        // visibility for the clocked in view.
        int clockActivityId = R.string.project_list_item_project_clock_in;
        int clockedInVisibility = View.GONE;
        if (project.isActive()) {
            clockActivityId = R.string.project_list_item_project_clock_out;
            clockedInVisibility = View.VISIBLE;
        }

        // Retrieve the resource instance.
        Resources resources = Worker.getContext().getResources();

        // Retrieve the string from the resources and update the view.
        String clockActivity = resources.getString(clockActivityId);
        projectViewHolder.getClockActivity().setText(clockActivity);

        // Set the visibility for the clocked in view.
        projectViewHolder.getClockedIn().setVisibility(clockedInVisibility);

        // Retrieve the time that the active session was clocked in.
        int clockedInSinceId = R.string.project_list_item_project_clocked_in_since;
        String clockedInSince = resources.getString(clockedInSinceId);
        clockedInSince = String.format(clockedInSince, project.getClockedInSince());
        projectViewHolder.getClockedInSince().setText(clockedInSince);

        // Add the onClickListener to the "Clock out at" item.
        projectViewHolder.getClockOutAt().setOnClickListener(this);
        projectViewHolder.getClockOutAt().setTag(index);
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
        // Retrieve the index from the view and
        // get the project based on the index.
        int index = (int) view.getTag();
        Project project = mProjects.get(index);

        switch (view.getId()) {
            case R.id.project_clock_activity:
                mActivityCallback.onProjectActivityToggle(project, index);
                break;
            case R.id.project_clock_out_at:
                mActivityCallback.onProjectClockOutAt(project, index);
                break;
            case R.id.project_list_item_card_view:
                mActivityCallback.onProjectOpen(project);
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
        protected TextView mClockedInSince;
        protected TextView mClockOutAt;

        public ProjectViewHolder(View view)
        {
            super(view);

            setName((TextView) view.findViewById(R.id.project_name));
            setTime((TextView) view.findViewById(R.id.project_time));
            setDescription((TextView) view.findViewById(R.id.project_description));
            setClockActivity((FlatButton) view.findViewById(R.id.project_clock_activity));
            setClockedIn((RelativeLayout) view.findViewById(R.id.project_clocked_in));
            setClockedInSince((TextView) view.findViewById(R.id.project_clocked_in_since));
            setClockOutAt((TextView) view.findViewById(R.id.project_clock_out_at));
        }

        public void setName(TextView name)
        {
            mName = name;
        }

        public TextView getName()
        {
            return mName;
        }

        public void setTime(TextView time)
        {
            mTime = time;
        }

        public TextView getTime()
        {
            return mTime;
        }

        public void setDescription(TextView description)
        {
            mDescription = description;
        }

        public TextView getDescription()
        {
            return mDescription;
        }

        public void setClockActivity(FlatButton clockActivity)
        {
            mClockActivity = clockActivity;
        }

        public FlatButton getClockActivity()
        {
            return mClockActivity;
        }

        public void setClockedIn(RelativeLayout clockedIn)
        {
            mClockedIn = clockedIn;
        }

        public RelativeLayout getClockedIn()
        {
            return mClockedIn;
        }

        public void setClockedInSince(TextView clockedInSince)
        {
            mClockedInSince = clockedInSince;
        }

        public TextView getClockedInSince()
        {
            return mClockedInSince;
        }

        public void setClockOutAt(TextView clockOutAt)
        {
            mClockOutAt = clockOutAt;
        }

        public TextView getClockOutAt()
        {
            return mClockOutAt;
        }
    }
}
