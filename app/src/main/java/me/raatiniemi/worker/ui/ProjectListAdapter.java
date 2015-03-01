package me.raatiniemi.worker.ui;

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
import me.raatiniemi.worker.util.DateIntervalFormatter;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ItemViewHolder>
{
    public interface OnProjectListListener
    {
        public void onProjectActivityToggle(Project project, int index);

        public void onProjectClockActivityAt(Project project, int index);

        public void onProjectOpen(Project project);
    }

    private OnProjectListListener mOnProjectListListener;

    private View.OnClickListener mOnClickListener;

    private ArrayList<Project> mProjects;

    public ProjectListAdapter(ArrayList<Project> projects)
    {
        mProjects = projects;

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Retrieve the index from the view and
                // get the project based on the index.
                int index = (int) view.getTag();
                Project project = mProjects.get(index);

                switch (view.getId()) {
                    case R.id.fragment_project_clock_activity_toggle:
                        mOnProjectListListener.onProjectActivityToggle(project, index);
                        break;
                    case R.id.fragment_project_clock_activity_at:
                        mOnProjectListListener.onProjectClockActivityAt(project, index);
                        break;
                    case R.id.fragment_project_list_item_card_view:
                        mOnProjectListListener.onProjectOpen(project);
                        break;
                }
            }
        };
    }

    @Override
    public int getItemCount()
    {
        return mProjects.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int index)
    {
        Project project = mProjects.get(index);

        // Add the on click listener for the card view.
        // Will open the single project activity.
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setTag(index);

        DateIntervalFormatter formatter = new DateIntervalFormatter();
        String summarize = formatter.format(project.summarizeTime());

        holder.getName().setText(project.getName());
        holder.getTime().setText(summarize);
        holder.getDescription().setText(project.getDescription());

        // If the project description is empty the view should be hidden.
        int visibility = View.VISIBLE;
        if (project.getDescription() == null || project.getDescription().isEmpty()) {
            visibility = View.GONE;
        }
        holder.getDescription().setVisibility(visibility);

        // Set row index and the click listener.
        holder.getClockActivityToggle().setTag(index);
        holder.getClockActivityToggle().setOnClickListener(mOnClickListener);

        // Depending on whether the project is active the text
        // for the clock activity view should be altered, and
        // visibility for the clocked activity view.
        int clockActivityToggleId = R.string.project_list_item_project_clock_in;
        int clockActivityAtId = R.string.project_list_item_project_clock_in_at;
        int clockedInSinceVisibility = View.GONE;
        if (project.isActive()) {
            clockActivityToggleId = R.string.project_list_item_project_clock_out;
            clockActivityAtId = R.string.project_list_item_project_clock_out_at;
            clockedInSinceVisibility = View.VISIBLE;
        }

        // Retrieve the resource instance.
        Resources resources = Worker.getContext().getResources();

        // Retrieve the string from the resources and update the view.
        String clockActivityToggle = resources.getString(clockActivityToggleId);
        holder.getClockActivityToggle().setText(clockActivityToggle);

        // Retrieve the time that the active session was clocked in.
        int clockedInSinceId = R.string.project_list_item_project_clocked_in_since;
        String clockedInSince = resources.getString(clockedInSinceId);
        clockedInSince = String.format(
            clockedInSince,
            project.getClockedInSince(),
            formatter.format(
                project.getElapsed(),
                DateIntervalFormatter.Type.HOURS_MINUTES
            )
        );
        holder.getClockedInSince().setText(clockedInSince);
        holder.getClockedInSince().setVisibility(clockedInSinceVisibility);

        // Add the onClickListener to the "Clock [in|out] at..." item.
        String clockActivityAt = resources.getString(clockActivityAtId);
        holder.getClockActivityAt().setText(clockActivityAt);
        holder.getClockActivityAt().setOnClickListener(mOnClickListener);
        holder.getClockActivityAt().setTag(index);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int index)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.fragment_project_list_item, viewGroup, false);

        return new ItemViewHolder(view);
    }

    public Project getItemAt(int index)
    {
        return mProjects.get(index);
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

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView mName;
        protected TextView mTime;
        protected TextView mDescription;
        protected FlatButton mClockActivityToggle;
        protected RelativeLayout mClockedActivity;
        protected TextView mClockedInSince;
        protected TextView mClockActivityAt;

        public ItemViewHolder(View view)
        {
            super(view);

            setName((TextView) view.findViewById(R.id.fragment_project_name));
            setTime((TextView) view.findViewById(R.id.fragment_project_time));
            setDescription((TextView) view.findViewById(R.id.fragment_project_description));
            setClockActivityToggle((FlatButton) view.findViewById(R.id.fragment_project_clock_activity_toggle));
            setClockedActivity((RelativeLayout) view.findViewById(R.id.fragment_project_clocked_activity));
            setClockedInSince((TextView) view.findViewById(R.id.fragment_project_clocked_in_since));
            setClockActivityAt((TextView) view.findViewById(R.id.fragment_project_clock_activity_at));
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

        public void setClockActivityToggle(FlatButton clockActivityToggle)
        {
            mClockActivityToggle = clockActivityToggle;
        }

        public FlatButton getClockActivityToggle()
        {
            return mClockActivityToggle;
        }

        public void setClockedActivity(RelativeLayout clockedActivity)
        {
            mClockedActivity = clockedActivity;
        }

        public RelativeLayout getClockedActivity()
        {
            return mClockedActivity;
        }

        public void setClockedInSince(TextView clockedInSince)
        {
            mClockedInSince = clockedInSince;
        }

        public TextView getClockedInSince()
        {
            return mClockedInSince;
        }

        public void setClockActivityAt(TextView clockActivityAt)
        {
            mClockActivityAt = clockActivityAt;
        }

        public TextView getClockActivityAt()
        {
            return mClockActivityAt;
        }
    }

    public void setOnProjectListListener(OnProjectListListener onProjectListListener)
    {
        mOnProjectListListener = onProjectListListener;
    }

    public OnProjectListListener getOnProjectListListener()
    {
        return mOnProjectListListener;
    }
}
