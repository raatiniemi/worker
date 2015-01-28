package me.raatiniemi.worker.domain;

import java.util.Date;

public class Time extends DomainObject
{
    private long mProjectId;

    private long mStart;

    private long mStop;

    public Time(Long id, long projectId, long start, long stop)
    {
        super(id);

        setProjectId(projectId);
        setStart(start);
        setStop(stop);
    }

    public Time(Long projectId)
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

    public void setStop(long stop)
    {
        mStop = stop;
    }

    public long getStop()
    {
        return mStop;
    }

    public void clockOut()
    {
        if (isActive()) {
            setStop((new Date()).getTime());
        }
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
