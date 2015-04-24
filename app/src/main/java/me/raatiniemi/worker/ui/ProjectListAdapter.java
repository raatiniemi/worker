package me.raatiniemi.worker.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.ListAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.util.DateIntervalFormat;
import me.raatiniemi.worker.util.HintedImageButtonListener;
import me.raatiniemi.worker.util.ProjectCollection;

public class ProjectListAdapter extends ListAdapter<ProjectCollection, ProjectListAdapter.ItemViewHolder>
{
    private static final String TAG = "ProjectListAdapter";

    private DateIntervalFormat mDateIntervalFormat;

    private OnItemClickListener mOnItemClickListener;

    private OnClockActivityChangeListener mOnClockActivityChangeListener;

    private View.OnClickListener mOnClickListener;

    private HintedImageButtonListener mHintedImageButtonListener;

    public ProjectListAdapter(Context context)
    {
        super(context);

        mDateIntervalFormat = new DateIntervalFormat();

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View with id " + v.getId() + " was clicked");

                // Check if the clicked view is the item view from the recycler view.
                if (v.getId() == R.id.fragment_project_list_item) {
                    // Check that the OnItemClickListener have been supplied.
                    if (null == getOnItemClickListener()) {
                        Log.e(TAG, "No OnItemClickListener have been supplied");
                        return;
                    }

                    // Relay the event with the item view to the OnItemClickListener.
                    getOnItemClickListener().onItemClick(v);
                } else {
                    int activityToggleId = R.id.fragment_project_clock_activity_toggle;
                    int activityAtId = R.id.fragment_project_clock_activity_at;

                    if (v.getId() == activityToggleId || v.getId() == activityAtId) {
                        // Check that the OnClockActivityChangeListener have been supplied.
                        if (null == getOnClockActivityChangeListener()) {
                            Log.e(TAG, "No OnClockActivityChangeListener have been supplied");
                            return;
                        }

                        // We have to navigate to the RecyclerView item view and send it to the
                        // listener. The item view is needed for positional data.
                        View view = (View) v.getParent().getParent().getParent();
                        if (null == view || !(view instanceof CardView)) {
                            Log.e(TAG, "Unable to locate the correct view for OnClockActivityChangeListener");
                            return;
                        }

                        // Depending on which ImageButton have been clicked the event should be
                        // sent to different methods on the OnClockActivityChangeListener.
                        if (v.getId() == activityToggleId) {
                            getOnClockActivityChangeListener().onClockActivityToggle(view);
                        } else {
                            getOnClockActivityChangeListener().onClockActivityAt(view);
                        }
                    } else {
                        Log.e(TAG, "Unhandled view with id " + v.getId());
                    }
                }
            }
        };

        mHintedImageButtonListener = new HintedImageButtonListener(getContext());
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int index)
    {
        Project project = get(index);

        // Add the on click listener for the card view.
        // Will open the single project activity.
        holder.itemView.setOnClickListener(mOnClickListener);

        String summarize = mDateIntervalFormat.format(project.summarizeTime());

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
        holder.mClockActivityToggle.setOnLongClickListener(mHintedImageButtonListener);
        holder.mClockActivityToggle.setActivated(project.isActive());

        // Add the onClickListener to the "Clock [in|out] at..." item.
        holder.mClockActivityAt.setOnClickListener(mOnClickListener);
        holder.mClockActivityAt.setOnLongClickListener(mHintedImageButtonListener);

        // Retrieve the resource instance.
        Resources resources = getContext().getResources();

        // Depending on whether the project is active the text
        // for the clock activity view should be altered, and
        // visibility for the clocked activity view.
        int clockedInSinceVisibility = View.GONE;
        int clockActivityToggleId = R.string.project_list_item_project_clock_in;
        int clockActivityAtId = R.string.project_list_item_project_clock_in_at;
        if (project.isActive()) {
            clockActivityToggleId = R.string.project_list_item_project_clock_out;
            clockActivityAtId = R.string.project_list_item_project_clock_out_at;
            clockedInSinceVisibility = View.VISIBLE;
        }

        String clockActivityToggle = resources.getString(clockActivityToggleId);
        holder.mClockActivityToggle.setContentDescription(clockActivityToggle);

        String clockActivityAt = resources.getString(clockActivityAtId);
        holder.mClockActivityAt.setContentDescription(clockActivityAt);

        // Retrieve the time that the active session was clocked in.
        int clockedInSinceId = R.string.project_list_item_project_clocked_in_since;
        String clockedInSince = resources.getString(clockedInSinceId);
        clockedInSince = String.format(
            clockedInSince,
            project.getClockedInSince(),
            mDateIntervalFormat.format(
                project.getElapsed(),
                DateIntervalFormat.Type.HOURS_MINUTES
            )
        );
        holder.mClockedInSince.setText(clockedInSince);
        holder.mClockedInSince.setVisibility(clockedInSinceVisibility);
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
        int position = getItems().size();
        getItems().add(project);

        // Notify the adapter that the project have been inserted.
        notifyItemInserted(position);

        // Return the new position, should scroll the recycler view.
        return position;
    }

    public void set(int position, Project project)
    {
        getItems().set(position, project);

        // Notify the adapter that the project have been modified.
        notifyItemChanged(position);
    }

    public Project get(int position)
    {
        return getItems().get(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mName;

        public TextView mTime;

        public TextView mDescription;

        public ImageButton mClockActivityToggle;

        public ImageButton mClockActivityAt;

        public TextView mClockedInSince;

        public ItemViewHolder(View view)
        {
            super(view);

            mName = (TextView) view.findViewById(R.id.fragment_project_name);
            mTime = (TextView) view.findViewById(R.id.fragment_project_time);
            mDescription = (TextView) view.findViewById(R.id.fragment_project_description);
            mClockActivityToggle = (ImageButton) view.findViewById(R.id.fragment_project_clock_activity_toggle);
            mClockActivityAt = (ImageButton) view.findViewById(R.id.fragment_project_clock_activity_at);
            mClockedInSince = (TextView) view.findViewById(R.id.fragment_project_clocked_in_since);
        }
    }

    public interface OnItemClickListener
    {
        public void onItemClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener()
    {
        return mOnItemClickListener;
    }

    public interface OnClockActivityChangeListener
    {
        public void onClockActivityToggle(View view);

        public void onClockActivityAt(View view);
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
