package me.raatiniemi.worker.util;

/**
 * Stores application constants.
 */
public final class Worker {
    /**
     * Package for the application.
     */
    public static final String PACKAGE = "me.raatiniemi.worker";

    /**
     * Name of the application database.
     */
    public static final String DATABASE_NAME = "worker";

    /**
     * Version of the application database structure.
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * Id for the notifications from the DataIntentService-class.
     */
    public static final int NOTIFICATION_DATA_INTENT_SERVICE_ID = 1;

    /**
     * Prefix for backup directories.
     */
    public static final String STORAGE_BACKUP_DIRECTORY_PREFIX = "backup-";
}
