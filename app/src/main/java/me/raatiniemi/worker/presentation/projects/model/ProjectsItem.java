/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.presentation.projects.model;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.util.DateIntervalFormat;
import me.raatiniemi.worker.presentation.util.HoursMinutesIntervalFormat;

public class ProjectsItem {
    private static final DateIntervalFormat intervalFormat;

    static {
        intervalFormat = new HoursMinutesIntervalFormat();
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"));
    private final Project project;
    private long registeredTimeSummary = 0;

    public ProjectsItem(Project project, List<Time> registeredTime) {
        this.project = project;

        calculateSummaryFromRegisteredTime(registeredTime);
    }

    public ProjectsItem(Project project) {
        this(project, project.getRegisteredTime());
    }

    private static void showTextView(TextView textView) {
        textView.setVisibility(View.VISIBLE);
    }

    private static void hideTextView(TextView textView) {
        textView.setVisibility(View.GONE);
    }

    private static String getClockedInSinceFormatTemplate(Resources resources) {
        return resources.getString(R.string.fragment_projects_item_clocked_in_since);
    }

    private void calculateSummaryFromRegisteredTime(List<Time> registeredTime) {
        for (Time interval : registeredTime) {
            registeredTimeSummary += interval.getTime();
        }
    }

    public Project asProject() {
        return project;
    }

    public String getTitle() {
        return project.getName();
    }

    public boolean isActive() {
        return project.isActive();
    }

    public String getTimeSummary() {
        long registeredTimeWithElapsed = registeredTimeSummary + project.getElapsed();

        return intervalFormat.format(registeredTimeWithElapsed);
    }

    public String getHelpTextForClockActivityToggle(Resources resources) {
        if (isActive()) {
            return resources.getString(R.string.fragment_projects_item_clock_out);
        }

        return resources.getString(R.string.fragment_projects_item_clock_in);
    }

    public String getHelpTextForClockActivityAt(Resources resources) {
        if (isActive()) {
            return resources.getString(R.string.fragment_projects_item_clock_out_at);
        }

        return resources.getString(R.string.fragment_projects_item_clock_in_at);
    }

    public String getClockedInSince(Resources resources) {
        if (!isActive()) {
            return null;
        }

        return String.format(
                Locale.forLanguageTag("en_US"),
                getClockedInSinceFormatTemplate(resources),
                getFormattedClockedInSince(),
                getFormattedElapsedTime()
        );
    }

    private String getFormattedClockedInSince() {
        // TODO: Handle if the time session overlap days.
        // The timestamp should include the date it was
        // checked in, e.g. 21 May 1:06PM.
        return timeFormat.format(project.getClockedInSince());
    }

    private String getFormattedElapsedTime() {
        return intervalFormat.format(project.getElapsed());
    }

    public void setVisibilityForClockedInSinceView(TextView clockedInSinceView) {
        if (isActive()) {
            showTextView(clockedInSinceView);
            return;
        }

        hideTextView(clockedInSinceView);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ProjectsItem)) {
            return false;
        }

        ProjectsItem that = (ProjectsItem) o;
        return project.equals(that.project);

    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + project.hashCode();
        return result;
    }
}
