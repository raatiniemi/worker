package me.raatiniemi.worker.domain;

import java.util.Date;

public class Time extends DomainObject
{
    private long mProjectId;

    private long mStart;

    private long mStop;

    public Time(Long id, Long projectId, Long start, Long stop)
    {
        super(id);
        setProjectId(projectId);
        setStart(start);
        setStop(stop);
    }

    private void setProjectId(Long projectId)
    {
        mProjectId = projectId;
    }

    public Long getProjectId()
    {
        return mProjectId;
    }

    public void setStart(Long start)
    {
        mStart = start;
    }

    public Long getStart()
    {
        return mStart;
    }

    public void setStop(Long stop)
    {
        mStop = stop;
    }

    public Long getStop()
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
