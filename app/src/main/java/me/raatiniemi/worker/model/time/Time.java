package me.raatiniemi.worker.model.time;

import java.util.Date;

import me.raatiniemi.worker.domain.DomainObject;
import me.raatiniemi.worker.exception.DomainException;

/**
 * Domain object for Time items.
 */
public class Time extends DomainObject {
    /**
     * Id for the project connected to the time item.
     */
    private long mProjectId;

    /**
     * Timestamp when time item starts.
     */
    private long mStart;

    /**
     * Timestamp when time item stops, or zero if still active.
     */
    private long mStop;

    /**
     * Constructor.
     *
     * @param id Id for the time item.
     * @param projectId Id for the project.
     * @param start Timestamp for start time.
     * @param stop Timestamp for stop time.
     * @throws DomainException If stop time is before start time, and stop is not zero.
     */
    public Time(Long id, long projectId, long start, long stop) throws DomainException {
        super(id);

        setProjectId(projectId);
        setStart(start);

        // Only set the stop time if the time is not active,
        // otherwise an exception will be thrown.
        if (stop > 0) {
            setStop(stop);
        }
    }

    /**
     * Constructor, short hand for clock in activity.
     *
     * @param projectId Id for the project.
     * @throws DomainException If stop time is before start time.
     */
    public Time(Long projectId) throws DomainException {
        this(null, projectId, (new Date()).getTime(), (long) 0);
    }

    /**
     * Get the project id to which the time is connected to.
     *
     * @return Id for connected project.
     */
    public long getProjectId() {
        return mProjectId;
    }

    /**
     * Set the project id to which the time is connected to.
     *
     * @param projectId Id for connected project.
     */
    private void setProjectId(long projectId) {
        mProjectId = projectId;
    }

    /**
     * Get the start time in milliseconds, represent the date and time
     * at which the object was considered clocked in.
     *
     * @return Start time in milliseconds.
     */
    public long getStart() {
        return mStart;
    }

    /**
     * Set the start time in milliseconds, represent the date and time (UNIX)
     * at which the object was considered clocked in.
     *
     * @param start Start time in milliseconds.
     */
    public void setStart(long start) {
        mStart = start;
    }

    /**
     * Get the stop time in milliseconds, represent the date and time (UNIX)
     * at which the object was considered clocked out.
     *
     * @return Stop time in milliseconds.
     */
    public long getStop() {
        return mStop;
    }

    /**
     * Set the stop time in milliseconds, represent the date and time (UNIX)
     * at which the object was considered clocked out.
     *
     * @param stop Stop time in milliseconds.
     * @throws DomainException If the value for stop is less than the value for start.
     */
    public void setStop(long stop) throws DomainException {
        // Check that the stop value is lager than the start value,
        // should not be able to clock out before clocked in.
        if (stop < getStart()) {
            throw new DomainException();
        }

        mStop = stop;
    }

    /**
     * Set clock in at given date.
     *
     * @param date Date to clock in.
     */
    public void clockInAt(Date date) {
        setStart(date.getTime());
    }

    /**
     * Set clock out at given date.
     *
     * @param date Date to clock out.
     * @throws DomainException If clock out date is before clock in date.
     */
    public void clockOutAt(Date date) throws DomainException {
        setStop(date.getTime());
    }

    /**
     * Check whether the time is active.
     *
     * @return True if time is active, otherwise false.
     */
    public boolean isActive() {
        return getStop() == 0;
    }

    /**
     * Retrieve the time in milliseconds between start and stop, or zero if time is active.
     *
     * @return Time in milliseconds between start and stop.
     */
    public long getTime() {
        long time = 0;

        if (!isActive()) {
            time = getStop() - getStart();
        }

        return time;
    }

    /**
     * Calculate the interval difference between start and stop. If time is
     * active the current time should be used, this way we can calculate the
     * elapsed time from when the time was clocked in.
     *
     * @return Interval in milliseconds.
     */
    public long getInterval() {
        long stop = getStop();

        if (isActive()) {
            stop = (new Date()).getTime();
        }

        return stop - getStart();
    }
}
