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

package me.raatiniemi.worker.presentation.model.timesheet;

import android.support.annotation.NonNull;

import java.util.Objects;

import me.raatiniemi.worker.domain.model.Time;

public final class TimeInAdapterResult implements Comparable<TimeInAdapterResult> {
    private final int mGroup;

    private final int mChild;

    private final Time mTime;

    private TimeInAdapterResult(int group, int child, Time time) {
        mGroup = group;
        mChild = child;
        mTime = time;
    }

    public static TimeInAdapterResult build(int group, int child, Time time) {
        return new TimeInAdapterResult(group, child, time);
    }

    public static TimeInAdapterResult build(TimeInAdapterResult result, Time time) {
        return build(result.getGroup(), result.getChild(), time);
    }

    public int getGroup() {
        return mGroup;
    }

    public int getChild() {
        return mChild;
    }

    public Time getTime() {
        return mTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TimeInAdapterResult)) {
            return false;
        }

        TimeInAdapterResult result = (TimeInAdapterResult) o;
        return mGroup == result.mGroup
                && mChild == result.mChild
                && Objects.equals(mTime, result.mTime);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + mGroup;
        result = 31 * result + mChild;
        result = 31 * result + Objects.hashCode(mTime);

        return result;
    }

    @Override
    public int compareTo(@NonNull TimeInAdapterResult rhs) {
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
