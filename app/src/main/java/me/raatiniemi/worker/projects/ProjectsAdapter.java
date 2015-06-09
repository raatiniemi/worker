package me.raatiniemi.worker.projects;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.ListAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.model.project.ProjectComparator;
import me.raatiniemi.worker.util.DateIntervalFormat;
import me.raatiniemi.worker.util.HintedImageButtonListener;
import me.raatiniemi.worker.model.project.ProjectCollection;

/**
 * Adapter for listing available projects.
 */
public class ProjectsAdapter extends ListAdapter<Project, ProjectCollection, ProjectsAdapter.ItemViewHolder> {
    /**
     * Tag for logging within the ProjectsAdapter.
     */
    private static final String TAG = "ProjectsAdapter";

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
     * @param clockActivityChangeListener Listener for clock activity changes.
     */
    public ProjectsAdapter(Context context, OnClockActivityChangeListener clockActivityChangeListener) {
        super(context);
        mOnClockActivityChangeListener = clockActivityChangeListener;
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
        final Project project = get(index);

        // Add the on click listener for the card view.
        // Will open the single project activity.
        holder.itemView.setOnClickListener(getOnClickListener());

        String summarize = DateIntervalFormat.format(project.summarizeTime());

        holder.mName.setText(project.getName());
        holder.mTime.setText(summarize);
        holder.mDescription.setText(project.getDescription());

        // If the project description is empty the view should be hidden.
        int visibility = View.VISIBLE;
        if (TextUtils.isEmpty(project.getDescription())) {
            visibility = View.GONE;
        }
        holder.mDescription.setVisibility(visibility);

        holder.mClockActivityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClockActivityChangeListener.onClockActivityToggle(project);
            }
        });
        holder.mClockActivityToggle.setOnLongClickListener(getHintedImageButtonListener());
        holder.mClockActivityToggle.setActivated(project.isActive());

        // Add the onClickListener to the "Clock [in|out] at..." item.
        holder.mClockActivityAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClockActivityChangeListener.onClockActivityAt(project);
            }
        });
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
        // TODO: Handle if the time session overlap days.
        // The timestamp should include the date it was
        // checked in, e.g. 21 May 1:06PM.
        Date clockedInSince = project.getClockedInSince();
        String clockedInSinceText = null;
        if (null != clockedInSince) {
            clockedInSinceText = resources.getString(R.string.projects_item_project_clocked_in_since);
            clockedInSinceText = String.format(
                clockedInSinceText,
                (new SimpleDateFormat("HH:mm")).format(clockedInSince),
                DateIntervalFormat.format(
                    project.getElapsed(),
                    DateIntervalFormat.Type.HOURS_MINUTES
                )
            );
        }
        holder.mClockedInSince.setText(clockedInSinceText);
        holder.mClockedInSince.setVisibility(clockedInSinceVisibility);
    }

    public int findProject(Project project) {
        return Collections.binarySearch(getItems(), project, new ProjectComparator());
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
         * @param project Project to change the clock activity.
         */
        void onClockActivityToggle(Project project);

        /**
         * Toggle the clock activity change, with date and time.
         *
         * @param project Project to change the clock activity.
         */
        void onClockActivityAt(Project project);
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
}
