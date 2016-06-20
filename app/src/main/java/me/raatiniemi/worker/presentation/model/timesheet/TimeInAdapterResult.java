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

import me.raatiniemi.worker.domain.model.Time;

public final class TimeInAdapterResult {
    private final int mGroup;

    private final int mChild;

    private final Time mTime;

    public TimeInAdapterResult(int group, int child, Time time) {
        mGroup = group;
        mChild = child;
        mTime = time;
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
}
