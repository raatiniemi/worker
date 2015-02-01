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

    public String summarizeTime()
    {
        // TODO: Migrate to date interval handler.

        // total time in number of seconds.
        long total = getTime();

        // Convert milliseconds to seconds.
        total = total / 1000;

        // Calculate the number of hours and minutes based
        // on the total number of seconds.
        long hours = (total / (60 * 60) % 24);
        long minutes = (total / 60 % 60);

        // If the number of seconds is at >= 30 we should add an extra minute
        // to the minutes, i.e. round up the minutes if they have passed 50%.
        //
        // Otherwise, total time of 49 seconds will still display 0m and not 1m.
        long seconds = (total % 60);
        if (seconds >= 30) {
            minutes += 1;
        }

        return String.format("%dh %dm", hours, minutes);
    }
}
