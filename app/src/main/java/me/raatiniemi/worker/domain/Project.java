package me.raatiniemi.worker.domain;

public class Project extends DomainObject
{
    private String mName;

    private String mDescription;

    public Project(Long id, String name)
    {
        super(id);
        setName(name);
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
}
