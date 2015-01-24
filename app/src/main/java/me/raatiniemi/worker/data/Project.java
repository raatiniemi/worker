package me.raatiniemi.worker.data;

import java.util.ArrayList;

public class Project
{
    private long mId;

    private String mName;

    private String mDescription;

    private ArrayList<Time> mTime;

    public Project(long id, String name)
    {
        setId(id);
        setName(name);
    }

    private void setId(long id)
    {
        mId = id;
    }

    public long getId()
    {
        return mId;
    }

    private void setName(String name)
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

    public void setTime(ArrayList<Time> time)
    {
        mTime = time;
    }

    public ArrayList<Time> getTime()
    {
        return mTime;
    }

    public String summarizeTime()
    {
        long totalTime = 0;

        ArrayList<Time> time = getTime();
        if (time != null && !time.isEmpty()) {
            for (Time item: time) {
                totalTime += item.getTime();
            }
        }

        long hours = totalTime / (60 * 60 * 1000) % 24;
        long minutes = totalTime / (60 * 1000) % 60;

        return String.format("%dh %dm", hours, minutes);
    }

    public boolean isActive()
    {
        boolean active = false;

        if (getTime() != null && !getTime().isEmpty()) {
            // Retrieve the last element of the time array and check if the
            // item is active, hence defines if the project is active.
            Time time = getTime().get(getTime().size() - 1);
            active = time.isActive();
        }

        return active;
    }
}
