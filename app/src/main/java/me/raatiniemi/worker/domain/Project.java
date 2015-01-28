package me.raatiniemi.worker.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Project extends DomainObject
{
    private String mName;

    private String mDescription;

    private ArrayList<Time> mTime;

    /**
     * Initialize an existing project.
     * @param id Id for the project.
     * @param name Name of the project.
     */
    public Project(Long id, String name)
    {
        super(id);

        setName(name);
        setTime(new ArrayList<Time>());
    }

    /**
     * Initialize a new project without an id.
     * @param name Name of the project.
     */
    public Project(String name)
    {
        this(null, name);
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

    private Time getActiveTime()
    {
        ArrayList<Time> time = getTime();

        if (time == null || time.isEmpty()) {
            return null;
        }

        return time.get(time.size() - 1);
    }

    public String getClockedInSince()
    {
        if (!isActive()) {
            return null;
        }

        // TODO: Handle if the time session overlap days.
        // The timestamp should include the date it was
        // checked in, e.g. 21 May 1:06PM.

        // Retrieve the last time, i.e. the active time session.
        Time time = getActiveTime();
        Date date = new Date(time.getStart());

        // Format the timestamp with hours and minutes.
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public Time clockOut()
    {
        if (!isActive()) {
            return null;
        }

        Time time = getActiveTime();
        time.clockOut();

        return time;
    }

    public boolean isActive()
    {
        boolean active = false;

        // Retrieve the last element of the time array and check if the
        // item is active, hence defines if the project is active.
        Time time = getActiveTime();
        if (time != null) {
            active = time.isActive();
        }

        return active;
    }
}
