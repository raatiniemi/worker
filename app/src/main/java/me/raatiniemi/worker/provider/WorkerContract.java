package me.raatiniemi.worker.provider;

import android.provider.BaseColumns;

public class WorkerContract
{
    public interface ProjectColumns
    {
        String ID = BaseColumns._ID;

        String NAME = "name";

        String DESCRIPTION = "description";

        String ARCHIVED = "archived";
    }

    public interface TimeColumns
    {
        String ID = BaseColumns._ID;

        String PROJECT_ID = "project_id";

        String START = "start";

        String STOP = "stop";
    }
}
