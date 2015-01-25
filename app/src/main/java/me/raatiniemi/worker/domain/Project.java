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

    public String summarizeTime()
    {
        // Total time in number of seconds.
        long total = 0;

        // TODO: Retrieve the time for the project.

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

    public boolean isActive()
    {
        // TODO: Implement the isActive method for the Project domain object.
        return false;
    }
}
