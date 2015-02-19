package me.raatiniemi.worker.domain;

import java.util.Date;

import me.raatiniemi.worker.exception.DomainException;

public class Time extends DomainObject
{
    private long mProjectId;

    private long mStart;

    private long mStop;

    public Time(Long id, long projectId, long start, long stop) throws DomainException
    {
        super(id);

        setProjectId(projectId);
        setStart(start);

        if (stop > 0) {
            setStop(stop);
        }
    }

    public Time(Long projectId) throws DomainException
    {
        this(null, projectId, (new Date()).getTime(), (long) 0);
    }

    private void setProjectId(long projectId)
    {
        mProjectId = projectId;
    }

    public long getProjectId()
    {
        return mProjectId;
    }

    public void setStart(long start)
    {
        mStart = start;
    }

    public long getStart()
    {
        return mStart;
    }

    public void setStop(long stop) throws DomainException
    {
        if (stop < getStart()) {
            throw new DomainException();
        }

        mStop = stop;
    }

    public long getStop()
    {
        return mStop;
    }

    public void clockInAt(Date date)
    {
        setStart(date.getTime());
    }

    public void clockOutAt(Date date) throws DomainException
    {
        setStop(date.getTime());
    }

    /**
     * Check whether the time is active.
     * @return True if time is active, otherwise false.
     */
    public boolean isActive()
    {
        return getStop() == 0;
    }

    /**
     * Retrieve the time in milliseconds between start and stop, or zero if time is active.
     * @return Time in milliseconds between start and stop.
     */
    public long getTime()
    {
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
     * @return Interval in milliseconds.
     */
    public long getInterval()
    {
        long stop = getStop();

        if (isActive()) {
            stop = (new Date()).getTime();
        }

        return stop - getStart();
    }
}
