package me.raatiniemi.worker.data;

public class Time
{
    private Long mId;

    private Long mProjectId;

    private Long mStart;

    private Long mStop;

    public void setId(Long id)
    {
        mId = id;
    }

    public void setId(long id)
    {
        setId(Long.valueOf(id));
    }

    public Long getId()
    {
        return mId;
    }

    public void setProjectId(Long projectId)
    {
        mProjectId = projectId;
    }

    public void setProjectId(long projectId)
    {
        setProjectId(Long.valueOf(projectId));
    }

    public Long getProjectId()
    {
        return mProjectId;
    }

    public void setStart(Long start)
    {
        mStart = start;
    }

    public void setStart(long start)
    {
        setStart(Long.valueOf(start));
    }

    public Long getStart()
    {
        return mStart;
    }

    public void setStop(Long stop)
    {
        mStop = stop;
    }

    public void setStop(long stop)
    {
        setStop(Long.valueOf(stop));
    }

    public Long getStop()
    {
        return mStop;
    }

    public boolean isActive()
    {
        return getStop() == null;
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
