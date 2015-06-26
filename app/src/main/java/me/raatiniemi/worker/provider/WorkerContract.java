package me.raatiniemi.worker.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class WorkerContract {
    public static final String AUTHORITY = "me.raatiniemi.worker";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private static final String PATH_PROJECTS = "projects";

    private static final String PATH_TIME = "time";

    /**
     * Name for the available tables within the database.
     */
    public interface Tables {
        /**
         * Name for the project table.
         */
        String PROJECT = "project";

        /**
         * Name for the registered time table.
         */
        String TIME = "time";
    }

    public interface ProjectColumns extends BaseColumns {
        String NAME = "name";

        String DESCRIPTION = "description";

        String ARCHIVED = "archived";
    }

    public interface TimeColumns extends BaseColumns {
        String PROJECT_ID = "project_id";

        String START = "start";

        String STOP = "stop";
    }

    public static class ProjectContract implements ProjectColumns {
        public static final String[] COLUMNS = {
            ProjectColumns._ID,
            ProjectColumns.NAME,
            ProjectColumns.DESCRIPTION,
            ProjectColumns.ARCHIVED
        };

        public static final String STREAM_TYPE =
            "vnd.android.cursor.dir/vnd.me.raatiniemi.worker.project";

        public static final String ITEM_TYPE =
            "vnd.android.cursor.item/vnd.me.raatiniemi.worker.project";

        private static final Uri STREAM_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_PROJECTS);

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
        public static Uri getItemUri(String id) {
            return Uri.withAppendedPath(getStreamUri(), id);
        }

        /**
         * Build the project time stream URI.
         *
         * @param id Id for the project.
         * @return Project time stream URI.
         */
        public static Uri getItemTimeUri(String id) {
            return Uri.withAppendedPath(getItemUri(id), PATH_TIME);
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

    public static class TimeContract implements TimeColumns {
        public static final String[] COLUMNS = {
            TimeColumns._ID,
            TimeColumns.PROJECT_ID,
            TimeColumns.START,
            TimeColumns.STOP
        };

        public static final String STREAM_TYPE =
            "vnd.android.cursor.dir/vnd.me.raatiniemi.worker.time";

        public static final String ITEM_TYPE =
            "vnd.android.cursor.item/vnd.me.raatiniemi.worker.time";

        private static final Uri STREAM_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_TIME);

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
        public static Uri getItemUri(String id) {
            return Uri.withAppendedPath(getStreamUri(), id);
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
