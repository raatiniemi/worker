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

    public boolean isActive()
    {
        return getStop() == 0;
    }

    public long getTime()
    {
        long time = 0;

        if (!isActive()) {
            time = getStop() - getStart();
        }

        return time;
    }
}
