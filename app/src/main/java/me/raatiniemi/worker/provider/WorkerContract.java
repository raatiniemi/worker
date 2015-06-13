package me.raatiniemi.worker.provider;

import android.provider.BaseColumns;

public class WorkerContract {
    public static final String CONTENT_AUTHORITY = "me.raatiniemi.worker";

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
        public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.me.raatiniemi.worker.project";

        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.me.raatiniemi.worker.project";
    }
}
