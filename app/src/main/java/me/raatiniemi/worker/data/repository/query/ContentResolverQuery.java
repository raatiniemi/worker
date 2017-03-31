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

package me.raatiniemi.worker.data.repository.query;

import me.raatiniemi.worker.domain.repository.query.Criteria;

import static me.raatiniemi.worker.util.NullUtil.isNull;

public class ContentResolverQuery {
    private final Criteria criteria;

    private ContentResolverQuery(Criteria criteria) {
        this.criteria = criteria;
    }

    public static ContentResolverQuery from(Criteria criteria) {
        return new ContentResolverQuery(criteria);
    }

    public String getSelection() {
        if (isNull(criteria)) {
            return null;
        }

        return criteria.getField() + criteria.getOperator() + "? COLLATE NOCASE";
    }

    public String[] getSelectionArgs() {
        if (isNull(criteria)) {
            return new String[]{};
        }

        return new String[]{criteria.getValue()};
    }
}
