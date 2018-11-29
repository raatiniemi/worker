/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.comparator;

import java.util.Comparator;
import java.util.Date;

public final class TimesheetDateComparator implements Comparator<Date> {
    @Override
    public int compare(Date o1, Date o2) {
        int comparable = o1.compareTo(o2);
        if (0 == comparable) {
            return 0;
        }

        return -comparable;
    }
}
