package me.raatiniemi.worker.model.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.DomainObject;
import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.domain.ClockActivityException;
import me.raatiniemi.worker.model.time.Time;

/**
 * Domain object for the Project.
 */
public class Project extends DomainObject {
    /**
     * Name for the project.
     */
    private String mName;

    /**
     * Description for the project.
     */
    private String mDescription;

    /**
     * Flag for archived project.
     */
    private Long mArchived;

    /**
     * Time registered for the project.
     */
    private List<Time> mTime;

    /**
     * Initialize an existing project.
     *
     * @param id Id for the project.
     * @param name Name of the project.
     */
    public Project(Long id, String name) {
        super(id);

        setName(name);
        setTime(new ArrayList<Time>());
        setArchived(0L);
    }

    /**
     * Initialize a new project without an id.
     *
     * @param name Name of the project.
     */
    public Project(String name) {
        this(null, name);
    }

    /**
     * Retrieve the project name.
     *
     * @return Project name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Set the project name.
     *
     * @param name Project name.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Retrieve the project description.
     *
     * @return Project description.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Set the project description.
     *
     * @param description Project description.
     */
    public void setDescription(String description) {
        mDescription = description;
    }

    /**
     * Retrieve the flag for archived project.
     *
     * @return Flag for archived project.
     */
    public Long getArchived() {
        return mArchived;
    }

    /**
     * Set the flag for archived project.
     *
     * @param archived Flag for archived project.
     */
    public void setArchived(Long archived) {
        mArchived = archived;
    }

    /**
     * Getter for the project time.
     *
     * @return Project time.
     */
    public List<Time> getTime() {
        return mTime;
    }

    /**
     * Setter for the project time.
     *
     * @param time Project time.
     */
    private void setTime(List<Time> time) {
        mTime = time;
    }

    /**
     * Add additional time for the project.
     *
     * @param time Time to add to the project.
     */
    public void addTime(Time time) {
        getTime().add(time);
    }

    /**
     * Add time for the project.
     *
     * @param time Time to add to the project.
     */
    public void addTime(List<Time> time) {
        getTime().addAll(time);
    }

    /**
     * Summarize the time for the project.
     *
     * @return Registered time in number of milliseconds.
     */
    public long summarizeTime() {
        // Total time in number of seconds.
        long total = 0;

        List<Time> time = getTime();
        if (null != time && !time.isEmpty()) {
            // Iterate of the registered time and
            // retrieve the time interval.
            for (Time item : time) {
                total += item.getTime();
            }
        }

        return total;
    }

    /**
     * Retrieve the elapsed time for an active project.
     *
     * @return Elapsed time in milliseconds, zero if project is not active.
     */
    public long getElapsed() {
        long elapsed = 0;

        if (isActive()) {
            // Retrieve the interval for the active time.
            Time time = getActiveTime();
            if (null != time) {
                elapsed = time.getInterval();
            }
        }

        return elapsed;
    }

    /**
     * Retrieve the time domain object that might be active.
     *
     * @return Time domain object, or null if no time have been registered.
     */
    private Time getActiveTime() {
        List<Time> time = getTime();

        if (time == null || time.isEmpty()) {
            return null;
        }

        return time.get(0);
    }

    /**
     * Retrieve the time when the project was clocked in.
     *
     * @return Time when project was clocked in, or null if project is not active.
     */
    public Date getClockedInSince() {
        // If the project is not active, there's nothing to do.
        if (!isActive()) {
            return null;
        }

        // Retrieve the last time, i.e. the active time session.
        Time time = getActiveTime();
        if (null == time) {
            return null;
        }

        return new Date(time.getStart());
    }

    /**
     * Clock in project at a given date and time, if project is active nothing happens.
     *
     * @param date Date and time for when to clock in the project.
     * @return The clocked time domain object, or null if project is active.
     */
    public Time clockInAt(Date date) throws DomainException {
        // If the project is already active, we can't clock in.
        if (isActive()) {
            throw new ClockActivityException("Unable to clock in, project is already active");
        }

        // Instantiate the Time domain object with the project
        // and clock in with the supplied date.
        Time time = new Time(this.getId());
        time.clockInAt(date);

        return time;
    }

    /**
     * Clock out project at a given date and time, if project is not active nothing happens.
     *
     * @param date Date and time for when to clock out the project.
     * @return The clocked out time domain object, or null if project is not active.
     */
    public Time clockOutAt(Date date) throws DomainException {
        // If the project is not active, we can't clock out.
        if (!isActive()) {
            throw new ClockActivityException("Unable to clock out, project is not active");
        }

        // Retrieve the active Time domain object,
        // and clock out with the supplied date.
        Time time = getActiveTime();
        if (null == time) {
            return null;
        }

        time.clockOutAt(date);
        return time;
    }

    /**
     * Clock out the active project, if the project is not active nothing happens.
     *
     * @return The clocked out time domain object, or null if project is not active.
     */
    public Time clockOut() throws DomainException {
        return clockOutAt(new Date());
    }

    /**
     * Check if the project is active.
     *
     * @return True if the project is active, otherwise false.
     */
    public boolean isActive() {
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
