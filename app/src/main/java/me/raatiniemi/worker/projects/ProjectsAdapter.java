package me.raatiniemi.worker.projects;

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

/**
 * Adapter for listing available projects.
 */
public class ProjectsAdapter extends ListAdapter<Project, ProjectCollection, ProjectsAdapter.ItemViewHolder> {
    /**
     * Tag for logging within the ProjectsAdapter.
     */
    private static final String TAG = "ProjectsAdapter";

    /**
     * On click listener for the project item related views.
     */
    private View.OnClickListener mOnClickListener = new OnProjectClickListener();

    /**
     * Handles formatting for date intervals.
     */
    private DateIntervalFormat mDateIntervalFormat;

    /**
     * Listener for toggling the clock activity.
     */
    private OnClockActivityChangeListener mOnClockActivityChangeListener;

    /**
     * Listener for hinting images.
     */
    private HintedImageButtonListener mHintedImageButtonListener;

    /**
     * Construct the ProjectsAdapter.
     *
     * @param context Context to use.
     * @param dateIntervalFormat Formatter for date intervals.
     */
    public ProjectsAdapter(Context context, DateIntervalFormat dateIntervalFormat) {
        super(context);

        // Populate the date interval formatter.
        mDateIntervalFormat = dateIntervalFormat;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.fragment_projects_item;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int index) {
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
        holder.mClockActivityToggle.setOnLongClickListener(getHintedImageButtonListener());
        holder.mClockActivityToggle.setActivated(project.isActive());

        // Add the onClickListener to the "Clock [in|out] at..." item.
        holder.mClockActivityAt.setOnClickListener(mOnClickListener);
        holder.mClockActivityAt.setOnLongClickListener(getHintedImageButtonListener());

        // Retrieve the resource instance.
        Resources resources = getContext().getResources();

        // Depending on whether the project is active the text
        // for the clock activity view should be altered, and
        // visibility for the clocked activity view.
        int clockedInSinceVisibility = View.GONE;
        int clockActivityToggleId = R.string.projects_item_project_clock_in;
        int clockActivityAtId = R.string.projects_item_project_clock_in_at;
        if (project.isActive()) {
            clockActivityToggleId = R.string.projects_item_project_clock_out;
            clockActivityAtId = R.string.projects_item_project_clock_out_at;
            clockedInSinceVisibility = View.VISIBLE;
        }

        String clockActivityToggle = resources.getString(clockActivityToggleId);
        holder.mClockActivityToggle.setContentDescription(clockActivityToggle);

        String clockActivityAt = resources.getString(clockActivityAtId);
        holder.mClockActivityAt.setContentDescription(clockActivityAt);

        // Retrieve the time that the active session was clocked in.
        int clockedInSinceId = R.string.projects_item_project_clocked_in_since;
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

    /**
     * Retrieve the listener for toggling the clock activity.
     *
     * @return Listener for toggling the clock activity, or null if none have been supplied.
     */
    public OnClockActivityChangeListener getOnClockActivityChangeListener() {
        return mOnClockActivityChangeListener;
    }

    /**
     * Set the listener for toggling the clock activity.
     *
     * @param onClockActivityChangeListener Listener for toggling the clock activity.
     */
    public void setOnClockActivityChangeListener(OnClockActivityChangeListener onClockActivityChangeListener) {
        mOnClockActivityChangeListener = onClockActivityChangeListener;
    }

    /**
     * Retrieve the listener for hinting images.
     *
     * @return Listener for hinting images, or null if none have been supplied.
     */
    public HintedImageButtonListener getHintedImageButtonListener() {
        return mHintedImageButtonListener;
    }

    /**
     * Set the listener for hinting images.
     *
     * @param hintedImageButtonListener Listener for hinting images.
     */
    public void setHintedImageButtonListener(HintedImageButtonListener hintedImageButtonListener) {
        mHintedImageButtonListener = hintedImageButtonListener;
    }

    /**
     * Listener interface for toggling the clock activity.
     */
    public interface OnClockActivityChangeListener {
        /**
         * Toggle the clock activity change.
         *
         * @param view View for the project item.
         */
        void onClockActivityToggle(View view);

        /**
         * Toggle the clock activity change, with date and time.
         *
         * @param view View for the project item.
         */
        void onClockActivityAt(View view);
    }

    /**
     * View holder for the project item view.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        /**
         * Field for the project name.
         */
        public TextView mName;

        /**
         * Field for the registered project time.
         */
        public TextView mTime;

        /**
         * Field for the project description.
         */
        public TextView mDescription;

        /**
         * Icon for toggling the project activity.
         */
        public ImageButton mClockActivityToggle;

        /**
         * Icon for toggling the project activity, with date and time.
         */
        public ImageButton mClockActivityAt;

        /**
         * Field for the time when the project was clocked in.
         */
        public TextView mClockedInSince;

        public ItemViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.fragment_project_name);
            mTime = (TextView) view.findViewById(R.id.fragment_project_time);
            mDescription = (TextView) view.findViewById(R.id.fragment_project_description);
            mClockActivityToggle = (ImageButton) view.findViewById(R.id.fragment_project_clock_activity_toggle);
            mClockActivityAt = (ImageButton) view.findViewById(R.id.fragment_project_clock_activity_at);
            mClockedInSince = (TextView) view.findViewById(R.id.fragment_project_clocked_in_since);
        }
    }

    /**
     * OnClickListener for the project item.
     */
    private class OnProjectClickListener implements View.OnClickListener {
        /**
         * Handles click events for project related views.
         *
         * @param v View that has been clicked.
         */
        @Override
        public void onClick(View v) {
            final int viewId = v.getId();
            Log.d(TAG, "View with id " + viewId + " was clicked");

            // Check if the clicked view is the item view from the recycler view.
            if (viewId == R.id.fragment_projects_item) {
                // Check that the OnItemClickListener have been supplied.
                if (null == getOnItemClickListener()) {
                    Log.e(TAG, "No OnItemClickListener have been supplied");
                    return;
                }

                // Relay the event with the item view to the OnItemClickListener.
                getOnItemClickListener().onItemClick(v);
            } else {
                final int activityToggleId = R.id.fragment_project_clock_activity_toggle;
                final int activityAtId = R.id.fragment_project_clock_activity_at;

                if (viewId == activityToggleId || viewId == activityAtId) {
                    // Check that the OnClockActivityChangeListener have been supplied.
                    if (null == getOnClockActivityChangeListener()) {
                        Log.e(TAG, "No OnClockActivityChangeListener have been supplied");
                        return;
                    }

                    // We have to navigate to the RecyclerView item view and send it to the
                    // listener. The item view is needed for positional data.
                    View view = (View) v.getParent().getParent().getParent();
                    if (null == view || !(view instanceof CardView)) {
                        Log.e(TAG, "Unable to locate the correct view for the OnClockActivityChangeListener");
                        return;
                    }

                    // Depending on which ImageButton have been clicked the event should be
                    // sent to different methods on the OnClockActivityChangeListener.
                    if (viewId == activityToggleId) {
                        getOnClockActivityChangeListener().onClockActivityToggle(view);
                    } else {
                        getOnClockActivityChangeListener().onClockActivityAt(view);
                    }
                } else {
                    Log.e(TAG, "Unhandled view with id " + viewId + " in the OnClickListener");
                }
            }
        }
    }
}
