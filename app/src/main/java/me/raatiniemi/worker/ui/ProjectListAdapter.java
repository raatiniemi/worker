package me.raatiniemi.worker.ui;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import me.raatiniemi.worker.util.DateIntervalFormat;
import me.raatiniemi.worker.util.ProjectCollection;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ItemViewHolder>
{
    private static final String TAG = "ProjectListAdapter";

    public interface OnItemClickListener
    {
        public void onItemClick(View view);
    }

    public interface OnClockActivityChangeListener
    {
        public void onClockActivityToggle(View view);

        public void onClockActivityAt(View view);
    }

    private OnItemClickListener mOnItemClickListener;

    private OnClockActivityChangeListener mOnClockActivityChangeListener;

    private View.OnClickListener mOnClickListener;

    private ProjectCollection mProjects;

    public ProjectListAdapter(ProjectCollection projects)
    {
        mProjects = projects;

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int activityToggle = R.id.fragment_project_clock_activity_toggle;
                int activityAt = R.id.fragment_project_clock_activity_at;

                if (R.id.fragment_project_list_item_card_view == v.getId()) {
                    if (null != getOnItemClickListener()) {
                        getOnItemClickListener().onItemClick(v);
                    } else {
                        Log.e("ProjectListAdapter", "No OnItemClickListener have been supplied");
                    }
                } else if (activityToggle == v.getId() || activityAt == v.getId()) {
                    if (null != getOnClockActivityChangeListener()) {
                        View view;
                        if (activityToggle == v.getId()) {
                            view = (View) v.getParent().getParent();
                            getOnClockActivityChangeListener().onClockActivityToggle(view);
                        } else {
                            view = (View) v.getParent().getParent().getParent();
                            getOnClockActivityChangeListener().onClockActivityAt(view);
                        }
                    } else {
                        Log.e("ProjectListAdapter", "No OnClockActivityChangeListener have been supplied");
                    }
                } else {
                    Log.e("ProjectListAdapter", "Unrecognized id: "+ v.getId());
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

        DateIntervalFormat intervalFormat = new DateIntervalFormat();
        String summarize = intervalFormat.format(project.summarizeTime());

        holder.mName.setText(project.getName());
        holder.mTime.setText(summarize);
        holder.mDescription.setText(project.getDescription());

        // If the project description is empty the view should be hidden.
        int visibility = View.VISIBLE;
        if (TextUtils.isEmpty(project.getDescription())) {
            visibility = View.GONE;
        }
        holder.mDescription.setVisibility(visibility);

        holder.mClockActivityToggle.setOnClickListener(mOnClickListener);

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
        holder.mClockActivityToggle.setText(clockActivityToggle);

        // Retrieve the time that the active session was clocked in.
        int clockedInSinceId = R.string.project_list_item_project_clocked_in_since;
        String clockedInSince = resources.getString(clockedInSinceId);
        clockedInSince = String.format(
            clockedInSince,
            project.getClockedInSince(),
            intervalFormat.format(
                project.getElapsed(),
                DateIntervalFormat.Type.HOURS_MINUTES
            )
        );
        holder.mClockedInSince.setText(clockedInSince);
        holder.mClockedInSince.setVisibility(clockedInSinceVisibility);

        // Add the onClickListener to the "Clock [in|out] at..." item.
        String clockActivityAt = resources.getString(clockActivityAtId);
        holder.mClockActivityAt.setText(clockActivityAt);
        holder.mClockActivityAt.setOnClickListener(mOnClickListener);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position)
    {
        return R.layout.fragment_project_list_item;
    }

    public int add(Project project)
    {
        // Retrieve the number of elements before adding the project,
        // hence getting the index of the new project.
        int position = mProjects.size();
        mProjects.add(project);

        // Notify the adapter that the project have been inserted.
        notifyItemInserted(position);

        // Return the new position, should scroll the recycler view.
        return position;
    }

    public void set(int position, Project project)
    {
        mProjects.set(position, project);

        // Notify the adapter that the project have been modified.
        notifyItemChanged(position);
    }

    public Project get(int position)
    {
        return mProjects.get(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mName;

        public TextView mTime;

        public TextView mDescription;

        public FlatButton mClockActivityToggle;

        public TextView mClockedInSince;

        public TextView mClockActivityAt;

        public ItemViewHolder(View view)
        {
            super(view);

            mName = (TextView) view.findViewById(R.id.fragment_project_name);
            mTime = (TextView) view.findViewById(R.id.fragment_project_time);
            mDescription = (TextView) view.findViewById(R.id.fragment_project_description);
            mClockActivityToggle = (FlatButton) view.findViewById(R.id.fragment_project_clock_activity_toggle);
            mClockedInSince = (TextView) view.findViewById(R.id.fragment_project_clocked_in_since);
            mClockActivityAt = (TextView) view.findViewById(R.id.fragment_project_clock_activity_at);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener()
    {
        return mOnItemClickListener;
    }

    public void setOnClockActivityChangeListener(OnClockActivityChangeListener onClockActivityChangeListener)
    {
        mOnClockActivityChangeListener = onClockActivityChangeListener;
    }

    public OnClockActivityChangeListener getOnClockActivityChangeListener()
    {
        return mOnClockActivityChangeListener;
    }
}
