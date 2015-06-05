package me.raatiniemi.worker.util;

/**
 * Easier handling of data and errors when using an AsyncTask.
 *
 * @param <T> The type of data to be used.
 */
public class AsyncTaskResult<T> {
    /**
     * Retrieved data from the AsyncTask.
     */
    private T mData;

    /**
     * Error that occurred during the AsyncTask.
     */
    private Throwable mError;

    /**
     * Construct the result with the retrieved data.
     *
     * @param data Data retrieved from the AsyncTask.
     */
    public AsyncTaskResult(T data) {
        mData = data;
    }

    /**
     * Construct the result with an error.
     *
     * @param error Error that occurred during the AsyncTask.
     */
    public AsyncTaskResult(Throwable error) {
        mError = error;
    }

    /**
     * Retrieve the data from the AsyncTask.
     *
     * @return Data retrieved from the AsyncTask, or null if an error occurred.
     */
    public T getData() {
        return mData;
    }

    /**
     * Retrieve the error that occurred during the AsyncTask.
     *
     * @return Error that occurred during AsyncTask, or null if none occurred.
     */
    public Throwable getError() {
        return mError;
    }
}
