package me.raatiniemi.worker.domain;

import java.util.ArrayList;

public class Project extends DomainObject
{
    private String mName;

    private String mDescription;

    private ArrayList<Time> mTime;

    public Project(Long id, String name)
    {
        super(id);
        setName(name);
        setTime(new ArrayList<Time>());
    }

    public void setName(String name)
    {
         mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public void setDescription(String description)
    {
        mDescription = description;
    }

    public String getDescription()
    {
        return mDescription;
    }

    private void setTime(ArrayList<Time> time)
    {
        mTime = time;
    }

    public ArrayList<Time> getTime()
    {
        return mTime;
    }

    public void addTime(Time time)
    {
        getTime().add(time);
    }

    public String summarizeTime()
    {
        // Total time in number of seconds.
        long total = 0;

        ArrayList<Time> time = getTime();
        if (null != time && !time.isEmpty()) {
            for (Time item: time) {
                total += item.getTime();
            }
        }

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

    private Time getLastTime()
    {
        ArrayList<Time> time = getTime();

        if (time == null || time.isEmpty()) {
            return null;
        }

        return time.get(time.size() - 1);
    }

    public Time clockOut()
    {
        if (!isActive()) {
            return null;
        }

        Time time = getLastTime();
        time.clockOut();

        return time;
    }

    public boolean isActive()
    {
        boolean active = false;

        // Retrieve the last element of the time array and check if the
        // item is active, hence defines if the project is active.
        Time time = getLastTime();
        if (time != null) {
            active = time.isActive();
        }

        return active;
    }
}
