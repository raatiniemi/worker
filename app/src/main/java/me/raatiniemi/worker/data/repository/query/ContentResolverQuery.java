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

package me.raatiniemi.worker.data.repository.query;

import me.raatiniemi.worker.domain.repository.query.Criteria;

public class ContentResolverQuery {
    private final Criteria mCriteria;

    private ContentResolverQuery(Criteria criteria) {
        mCriteria = criteria;
    }

    public static ContentResolverQuery from(Criteria criteria) {
        return new ContentResolverQuery(criteria);
    }

    public String getSelection() {
        if (null == mCriteria) {
            return null;
        }

        return mCriteria.getField() + mCriteria.getOperator() + "? COLLATE NOCASE";
    }

    public String[] getSelectionArgs() {
        if (null == mCriteria) {
            return null;
        }

        return new String[]{mCriteria.getValue()};
    }
}
