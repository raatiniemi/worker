package me.raatiniemi.worker.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class WorkerContract {
    public static final String CONTENT_AUTHORITY = "me.raatiniemi.worker";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_PROJECTS = "projects";

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

    public interface ProjectColumns {
        String ID = BaseColumns._ID;

        String NAME = "name";

        String DESCRIPTION = "description";

        String ARCHIVED = "archived";
    }

    public interface TimeColumns {
        String ID = BaseColumns._ID;

        String PROJECT_ID = "project_id";

        String START = "start";

        String STOP = "stop";
    }

    public static class Projects implements ProjectColumns {
        public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROJECTS).build();

        public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.me.raatiniemi.worker.project";

        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.me.raatiniemi.worker.project";

        /**
         * Build the URI for retrieving specific project with identifier.
         *
         * @param id Numeric id or name for project.
         * @return URI for retrieving specific project.
         */
        public static Uri buildUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        /**
         * Retrieve the identifier from the URI.
         *
         * @param uri URI for retrieving specific project.
         * @return Numeric id or name for project.
         */
        public static String getId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
