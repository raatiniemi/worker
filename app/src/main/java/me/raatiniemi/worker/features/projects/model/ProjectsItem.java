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

package me.raatiniemi.worker.features.projects.model;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.util.DateIntervalFormat;
import me.raatiniemi.worker.domain.util.HoursMinutesIntervalFormat;

public class ProjectsItem {
    private static final DateIntervalFormat intervalFormat;

    static {
        intervalFormat = new HoursMinutesIntervalFormat();
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"));
    private final Project project;
    private final long registeredTimeSummary;
    private final TimeInterval activeTimeInterval;

    private ProjectsItem(Project project, List<TimeInterval> registeredTime) {
        this.project = project;

        registeredTimeSummary = calculateSummaryFromRegisteredTime(registeredTime);
        activeTimeInterval = findActiveTimeInterval(registeredTime);
    }

    public static ProjectsItem from(Project project, List<TimeInterval> registeredTime) {
        return new ProjectsItem(project, registeredTime);
    }

    public static ProjectsItem from(Project project) {
        return ProjectsItem.from(project, project.getTimeIntervals());
    }

    private static long calculateSummaryFromRegisteredTime(List<TimeInterval> registeredTime) {
        long timeSummary = 0;

        for (TimeInterval interval : registeredTime) {
            timeSummary += interval.getTime();
        }

        return timeSummary;
    }

    @Nullable
    private static TimeInterval findActiveTimeInterval(@Nonnull List<TimeInterval> registeredTime) {
        for (TimeInterval timeInterval : registeredTime) {
            if (timeInterval.isActive()) {
                return timeInterval;
            }
        }

        return null;
    }

    private static String formattedElapsedTime(long elapsedTimeInMilliseconds) {
        return intervalFormat.format(elapsedTimeInMilliseconds);
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
        return intervalFormat.format(calculateTimeSummary());
    }

    private long calculateTimeSummary() {
        if (isActive()) {
            return registeredTimeSummary + activeTimeInterval.getInterval();
        }

        return registeredTimeSummary;
    }

    public String getHelpTextForClockActivityToggle(Resources resources) {
        if (isActive()) {
            return resources.getString(R.string.fragment_projects_item_clock_out, project.getName());
        }

        return resources.getString(R.string.fragment_projects_item_clock_in, project.getName());
    }

    public String getHelpTextForClockActivityAt(Resources resources) {
        if (isActive()) {
            return resources.getString(R.string.fragment_projects_item_clock_out_at, project.getName());
        }

        return resources.getString(R.string.fragment_projects_item_clock_in_at, project.getName());
    }

    public String getHelpTextForDelete(Resources resources) {
        return resources.getString(R.string.fragment_projects_item_delete, project.getName());
    }

    public String getClockedInSince(Resources resources) {
        if (!isActive()) {
            return null;
        }

        return String.format(
                Locale.forLanguageTag("en_US"),
                getClockedInSinceFormatTemplate(resources),
                getFormattedClockedInSince(),
                formattedElapsedTime(activeTimeInterval.getInterval())
        );
    }

    private String getFormattedClockedInSince() {
        // TODO: Handle if the time session overlap days.
        // The timestamp should include the date it was
        // checked in, e.g. 21 May 1:06PM.
        Date date = new Date(getClockedInSinceInMilliseconds());
        return timeFormat.format(date);
    }

    public long getClockedInSinceInMilliseconds() {
        if (isActive()) {
            return activeTimeInterval.getStartInMilliseconds();
        }

        return 0;
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
