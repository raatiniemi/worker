/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.model;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.util.DateIntervalFormat;

public class ProjectsModel {
    private final Project mProject;

    public ProjectsModel(Project project) {
        mProject = project;
    }

    public Project asProject() {
        return mProject;
    }

    public String getTitle() {
        return mProject.getName();
    }

    public String getDescription() {
        return mProject.getDescription();
    }

    public void setVisibilityForDescriptionView(TextView descriptionView) {
        if (null == getDescription() || 0 == getDescription().length()) {
            descriptionView.setVisibility(View.GONE);
            return;
        }

        descriptionView.setVisibility(View.VISIBLE);
    }

    public String getTimeSummary() {
        return DateIntervalFormat.format(
                mProject.summarizeTime()
        );
    }

    public String getClockedInSince(Resources resources) {
        // Retrieve the time that the active session was clocked in.
        // TODO: Handle if the time session overlap days.
        // The timestamp should include the date it was
        // checked in, e.g. 21 May 1:06PM.
        Date clockedInSince = mProject.getClockedInSince();
        String clockedInSinceText = null;
        if (null != clockedInSince) {
            clockedInSinceText = resources.getString(R.string.fragment_projects_item_clocked_in_since);
            clockedInSinceText = String.format(
                    clockedInSinceText,
                    (new SimpleDateFormat("HH:mm", Locale.getDefault())).format(clockedInSince),
                    DateIntervalFormat.format(
                            mProject.getElapsed(),
                            DateIntervalFormat.Type.HOURS_MINUTES
                    )
            );
        }
        return clockedInSinceText;
    }
}
