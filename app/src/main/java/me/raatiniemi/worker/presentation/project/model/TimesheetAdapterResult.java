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

package me.raatiniemi.worker.presentation.project.model;

import android.support.annotation.NonNull;

import java.util.Objects;

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.model.TimesheetItem;

public final class TimesheetAdapterResult implements Comparable<TimesheetAdapterResult> {
    private final int group;

    private final int child;

    private final TimesheetItem item;

    private TimesheetAdapterResult(int group, int child, TimesheetItem item) {
        this.group = group;
        this.child = child;
        this.item = item;
    }

    public static TimesheetAdapterResult build(int group, int child, TimesheetItem item) {
        return new TimesheetAdapterResult(group, child, item);
    }

    public static TimesheetAdapterResult build(TimesheetAdapterResult result, TimesheetItem item) {
        return build(result.getGroup(), result.getChild(), item);
    }

    public int getGroup() {
        return group;
    }

    public int getChild() {
        return child;
    }

    public Time getTime() {
        return item.asTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TimesheetAdapterResult)) {
            return false;
        }

        TimesheetAdapterResult result = (TimesheetAdapterResult) o;
        return group == result.group
                && child == result.child
                && Objects.equals(item, result.item);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + group;
        result = 31 * result + child;
        result = 31 * result + Objects.hashCode(item);

        return result;
    }

    @Override
    public int compareTo(@NonNull TimesheetAdapterResult rhs) {
        if (getGroup() == rhs.getGroup()) {
            if (getChild() == rhs.getChild()) {
                return 0;
            }

            if (getChild() > rhs.getChild()) {
                return 1;
            }

            return -1;
        }

        if (getGroup() > rhs.getGroup()) {
            return 1;
        }

        return -1;
    }
}
