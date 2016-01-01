/*
 * Copyright (C) 2015 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.presentation.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
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
import java.util.List;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.base.view.adapter.SimpleListAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.comparator.ProjectComparator;
import me.raatiniemi.worker.util.DateIntervalFormat;
import me.raatiniemi.worker.util.HintedImageButtonListener;

/**
 * Adapter for listing available projects.
 */
public class ProjectsAdapter extends SimpleListAdapter<Project, ProjectsAdapter.ItemViewHolder> {
    /**
     * Tag for logging within the ProjectsAdapter.
     */
    private static final String TAG = "ProjectsAdapter";

    /**
     * Listener for project actions.
     */
    private final OnProjectActionListener mOnProjectActionListener;

    /**
     * Listener for hinting images.
     */
    private HintedImageButtonListener mHintedImageButtonListener;

    /**
     * Construct the ProjectsAdapter.
     *
     * @param context               Context to use.
     * @param items                 Items for the adapter.
     * @param projectActionListener Listener for project actions.
     */
    public ProjectsAdapter(
            Context context,
            @NonNull List<Project> items,
            OnProjectActionListener projectActionListener
    ) {
        super(context, items);

        mOnProjectActionListener = projectActionListener;
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
    public void onBindViewHolder(final ItemViewHolder vh, int index) {
        final Project project = get(index);

        // Add the on click listener for the card view.
        // Will open the single project activity.
        vh.itemView.setOnClickListener(getOnClickListener());

        String summarize = DateIntervalFormat.format(project.summarizeTime());

        vh.mName.setText(project.getName());
        vh.mTime.setText(summarize);
        vh.mDescription.setText(project.getDescription());

        // If the project description is empty the view should be hidden.
        int visibility = View.VISIBLE;
        if (TextUtils.isEmpty(project.getDescription())) {
            visibility = View.GONE;
        }
        vh.mDescription.setVisibility(visibility);

        vh.mClockActivityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnProjectActionListener.onClockActivityToggle(project);
            }
        });
        vh.mClockActivityToggle.setOnLongClickListener(getHintedImageButtonListener());
        vh.mClockActivityToggle.setActivated(project.isActive());

        // Add the onClickListener to the "Clock [in|out] at..." item.
        vh.mClockActivityAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnProjectActionListener.onClockActivityAt(project);
            }
        });
        vh.mClockActivityAt.setOnLongClickListener(getHintedImageButtonListener());

        // Add the onClickListener to the "Delete project" item.
        vh.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnProjectActionListener.onDelete(project);
            }
        });
        vh.mClockActivityAt.setOnLongClickListener(getHintedImageButtonListener());

        // Retrieve the resource instance.
        Resources resources = getContext().getResources();

        // Depending on whether the project is active the text
        // for the clock activity view should be altered, and
        // visibility for the clocked activity view.
        int clockedInSinceVisibility = View.GONE;
        int clockActivityToggleId = R.string.fragment_projects_item_clock_in;
        int clockActivityAtId = R.string.fragment_projects_item_clock_in_at;
        if (project.isActive()) {
            clockActivityToggleId = R.string.fragment_projects_item_clock_out;
            clockActivityAtId = R.string.fragment_projects_item_clock_out_at;
            clockedInSinceVisibility = View.VISIBLE;
        }

        String clockActivityToggle = resources.getString(clockActivityToggleId);
        vh.mClockActivityToggle.setContentDescription(clockActivityToggle);

        String clockActivityAt = resources.getString(clockActivityAtId);
        vh.mClockActivityAt.setContentDescription(clockActivityAt);

        // Retrieve the time that the active session was clocked in.
        // TODO: Handle if the time session overlap days.
        // The timestamp should include the date it was
        // checked in, e.g. 21 May 1:06PM.
        Date clockedInSince = project.getClockedInSince();
        String clockedInSinceText = null;
        if (null != clockedInSince) {
            clockedInSinceText = resources.getString(R.string.fragment_projects_item_clocked_in_since);
            clockedInSinceText = String.format(
                    clockedInSinceText,
                    (new SimpleDateFormat("HH:mm", Locale.getDefault())).format(clockedInSince),
                    DateIntervalFormat.format(
                            project.getElapsed(),
                            DateIntervalFormat.Type.HOURS_MINUTES
                    )
            );
        }
        vh.mClockedInSince.setText(clockedInSinceText);
        vh.mClockedInSince.setVisibility(clockedInSinceVisibility);
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
     * View holder for the project item view.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        /**
         * Field for the project name.
         */
        public final TextView mName;

        /**
         * Field for the registered project time.
         */
        public final TextView mTime;

        /**
         * Field for the project description.
         */
        public final TextView mDescription;

        /**
         * Icon for toggling the project activity.
         */
        public final ImageButton mClockActivityToggle;

        /**
         * Icon for toggling the project activity, with date and time.
         */
        public final ImageButton mClockActivityAt;

        /**
         * Icon for deleting project.
         */
        public final ImageButton mDelete;

        /**
         * Field for the time when the project was clocked in.
         */
        public final TextView mClockedInSince;

        public ItemViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.fragment_projects_item_name);
            mTime = (TextView) view.findViewById(R.id.fragment_projects_item_time);
            mDescription = (TextView) view.findViewById(R.id.fragment_projects_item_description);
            mClockActivityToggle = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_clock_activity_toggle);
            mClockActivityAt = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_clock_activity_at);
            mDelete = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_delete);
            mClockedInSince = (TextView) view.findViewById(R.id.fragment_projects_item_clocked_in_since);
        }
    }

    /**
     * Interface for project actions.
     */
    public interface OnProjectActionListener {
        /**
         * Toggle the clock activity change.
         *
         * @param project Project to change the clock activity.
         */
        void onClockActivityToggle(@NonNull Project project);

        /**
         * Toggle the clock activity change, with date and time.
         *
         * @param project Project to change the clock activity.
         */
        void onClockActivityAt(@NonNull Project project);

        /**
         * Handle project delete action from user.
         *
         * @param project Project to delete.
         */
        void onDelete(@NonNull Project project);
    }
}
