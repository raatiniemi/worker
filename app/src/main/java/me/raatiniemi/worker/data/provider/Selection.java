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

package me.raatiniemi.worker.data.provider;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;

final class Selection {
    private final String table;
    private final String selection;
    private final String[] selectionArgs;
    private final String groupBy;

    private Selection(Builder builder) {
        table = builder.table;
        selection = builder.selection();
        selectionArgs = builder.selectionArgs();
        groupBy = builder.groupBy;
    }

    static Builder builder() {
        return new Builder();
    }

    String getTable() {
        return table;
    }

    String getSelection() {
        return selection;
    }

    String[] getSelectionArgs() {
        if (isNull(selectionArgs)) {
            return null;
        }

        return selectionArgs.clone();
    }

    String getGroupBy() {
        return groupBy;
    }

    static final class Builder {
        private final StringBuilder selection = new StringBuilder();
        private final List<String> selectionArgs = new ArrayList<>();
        private String table;
        private String groupBy;

        private Builder() {
        }

        Builder table(String table) {
            this.table = table;
            return this;
        }

        Builder where(String selection, String... selectionArgs) {
            // If the selection is empty, we can continue.
            if (TextUtils.isEmpty(selection)) {
                return this;
            }

            // If we are using multiple selections we need to
            // match all of the selections.
            if (0 < this.selection.length()) {
                this.selection.append(" AND ");
            }

            // In case we are using multiple selections we have
            // to encapsulate each of the selections.
            this.selection.append("(").append(selection).append(")");
            if (nonNull(selectionArgs)) {
                Collections.addAll(this.selectionArgs, selectionArgs);
            }
            return this;
        }

        private String selection() {
            return selection.toString();
        }

        private String[] selectionArgs() {
            return selectionArgs.toArray(new String[selectionArgs.size()]);
        }

        Builder groupBy(String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        Selection build() {
            return new Selection(this);
        }
    }
}
