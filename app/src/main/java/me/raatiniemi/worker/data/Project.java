package me.raatiniemi.worker.data;

public class Project
{
    private long mId;

    private String mName;

    private String mDescription;

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
}
