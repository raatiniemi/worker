/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class WorkerContract {
    /**
     * Name for the offset query parameter.
     */
    public static final String QUERY_PARAMETER_OFFSET = "offset";

    /**
     * Name for the limit query parameter.
     */
    public static final String QUERY_PARAMETER_LIMIT = "limit";

    public static final String AUTHORITY = "me.raatiniemi.worker";

    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private static final String PATH_PROJECTS = "projects";

    private static final String PATH_TIMESHEET = "timesheet";

    private static final String PATH_TIME = "time";

    /**
     * Name for the available tables within the database.
     */
    public final class Tables {
        /**
         * Name for the project table.
         */
        public static final String PROJECT = "project";

        /**
         * Name for the registered time table.
         */
        public static final String TIME = "time";

        private Tables() {
        }
    }

    public final class ProjectColumns {
        public static final String NAME = "name";

        public static final String DESCRIPTION = "description";

        public static final String ARCHIVED = "archived";

        private ProjectColumns() {
        }
    }

    public final class TimeColumns {
        public static final String PROJECT_ID = "project_id";

        public static final String START = "start";

        public static final String STOP = "stop";

        public static final String REGISTERED = "registered";

        private TimeColumns() {
        }
    }

    public static class ProjectContract {
        public static final String STREAM_TYPE =
                "vnd.android.cursor.dir/vnd.me.raatiniemi.worker.project";

        public static final String ITEM_TYPE =
                "vnd.android.cursor.item/vnd.me.raatiniemi.worker.project";

        /**
         * Order by clause for project time.
         */
        public static final String ORDER_BY_TIME =
                TimeColumns.STOP + " ASC," + TimeColumns.START + " ASC";

        /**
         * Group by clause for timesheet.
         */
        public static final String GROUP_BY_TIMESHEET =
                "strftime('%Y%m%d', " + TimeColumns.START + " / 1000, 'unixepoch')";

        /**
         * Order by clause for timesheet.
         */
        public static final String ORDER_BY_TIMESHEET =
                TimeColumns.START + " DESC," + TimeColumns.STOP + " DESC";

        private static final Uri STREAM_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_PROJECTS);

        private ProjectContract() {
        }

        public static String[] getColumns() {
            return new String[]{
                    BaseColumns._ID,
                    ProjectColumns.NAME
            };
        }

        public static String[] getTimesheetColumns() {
            return new String[]{
                    "MIN(" + TimeColumns.START + ") AS date",
                    "GROUP_CONCAT(" + BaseColumns._ID + ")"
            };
        }

        /**
         * Get the project stream URI.
         *
         * @return Project stream URI.
         */
        public static Uri getStreamUri() {
            return STREAM_URI;
        }

        /**
         * Build the URI for working with a specific project.
         *
         * @param id Id for the project.
         * @return URI for working with specific project.
         */
        public static Uri getItemUri(final long id) {
            return Uri.withAppendedPath(getStreamUri(), String.valueOf(id));
        }

        /**
         * Build the project time stream URI.
         *
         * @param id Id for the project.
         * @return Project time stream URI.
         */
        public static Uri getItemTimeUri(final long id) {
            return Uri.withAppendedPath(getItemUri(id), PATH_TIME);
        }

        /**
         * Build the project timesheet stream URI.
         *
         * @param id Id for the project.
         * @return Project timesheet stream URI.
         */
        public static Uri getItemTimesheetUri(final long id) {
            return Uri.withAppendedPath(getItemUri(id), PATH_TIMESHEET);
        }

        /**
         * Retrieve the identifier from the project URI.
         *
         * @param uri URI for working with specific project.
         * @return Id for the project.
         */
        public static String getItemId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class TimeContract {
        public static final String STREAM_TYPE =
                "vnd.android.cursor.dir/vnd.me.raatiniemi.worker.time";

        public static final String ITEM_TYPE =
                "vnd.android.cursor.item/vnd.me.raatiniemi.worker.time";

        private static final Uri STREAM_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_TIME);

        private TimeContract() {
        }

        public static String[] getColumns() {
            return new String[]{
                    BaseColumns._ID,
                    TimeColumns.PROJECT_ID,
                    TimeColumns.START,
                    TimeColumns.STOP,
                    TimeColumns.REGISTERED
            };
        }

        /**
         * Get the time stream URI.
         *
         * @return Time stream URI.
         */
        public static Uri getStreamUri() {
            return STREAM_URI;
        }

        /**
         * Build the URI for working with a specific time item.
         *
         * @param id Id for the time row.
         * @return URI for working with specific time item.
         */
        public static Uri getItemUri(final long id) {
            return Uri.withAppendedPath(getStreamUri(), String.valueOf(id));
        }

        /**
         * Retrieve the identifier from the time URI.
         *
         * @param uri URI for working with specific time item.
         * @return Id for the time item.
         */
        public static String getItemId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
