package me.raatiniemi.worker.data;

public class Project
{
    private long mId;

    private String mName;

    private String mDescription;

    public Project(long id, String name, String description)
    {
        mId = id;
        mName = name;
        mDescription = description;
    }

    public long getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public String getDescription()
    {
        return mDescription;
    }
}
