package me.raatiniemi.worker.model.project;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.DomainObject;
import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.domain.ClockActivityException;
import me.raatiniemi.worker.model.time.Time;

/**
 * Represent a project.
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
     * Constructor.
     *
     * @param id Id for the project.
     * @param name Name of the project.
     */
    public Project(@Nullable Long id, @NonNull String name) {
        super(id);

        setName(name);
        setTime(new ArrayList<Time>());
        setArchived(0L);
    }

    /**
     * Constructor, used for creating new projects.
     *
     * @param name Name of the project.
     */
    public Project(@NonNull String name) {
        this(null, name);
    }

    /**
     * Getter method for the project name.
     *
     * @return Project name.
     */
    @NonNull
    public String getName() {
        return mName;
    }

    /**
     * Setter method for the project name.
     *
     * @param name Project name.
     */
    public void setName(@NonNull String name) {
        mName = name;
    }

    /**
     * Getter method for the project description.
     *
     * @return Project description.
     */
    @Nullable
    public String getDescription() {
        return mDescription;
    }

    /**
     * Setter method for the project description.
     *
     * @param description Project description.
     */
    public void setDescription(@Nullable String description) {
        // If the description is empty we should reset it to null.
        if (TextUtils.isEmpty(description)) {
            description = null;
        }

        mDescription = description;
    }

    /**
     * Getter method for the archived project flag.
     *
     * @return Flag for archived project.
     */
    public Long getArchived() {
        return mArchived;
    }

    /**
     * Setter method for archived project flag.
     *
     * @param archived Flag for archived project.
     */
    public void setArchived(Long archived) {
        mArchived = archived;
    }

    /**
     * Getter method for the project time.
     *
     * @return Project time.
     */
    @NonNull
    public List<Time> getTime() {
        return mTime;
    }

    /**
     * Internal setter method for the project time.
     *
     * @param time Project time.
     */
    private void setTime(@NonNull List<Time> time) {
        mTime = time;
    }

    /**
     * Add time for the project.
     *
     * @param time Time to add to the project.
     */
    public void addTime(@NonNull Time time) {
        getTime().add(time);
    }

    /**
     * Add time for the project from a list.
     *
     * @param time Time to add to the project.
     */
    public void addTime(@NonNull List<Time> time) {
        // If the list with items are empty, there's no
        // need to attempt to add them.
        if (time.isEmpty()) {
            return;
        }

        getTime().addAll(time);
    }

    /**
     * Summarize the registered time for the project in milliseconds.
     *
     * @return Registered time in milliseconds.
     */
    public long summarizeTime() {
        // Total time in number of seconds.
        long total = 0;

        List<Time> time = getTime();
        if (!time.isEmpty()) {
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

        // Retrieve the interval for the active time.
        Time time = getActiveTime();
        if (null != time && time.isActive()) {
            elapsed = time.getInterval();
        }

        return elapsed;
    }

    /**
     * Retrieve the time domain object that might be active.
     *
     * @return Time domain object, or null if no time have been registered.
     */
    @Nullable
    private Time getActiveTime() {
        List<Time> time = getTime();
        if (time.isEmpty()) {
            return null;
        }

        return time.get(0);
    }

    /**
     * Retrieve the date when the project was clocked in.
     *
     * @return Date when project was clocked in, or null if project is not active.
     */
    @Nullable
    public Date getClockedInSince() {
        // Retrieve the last time, i.e. the active time session.
        Time time = getActiveTime();
        if (null == time || !time.isActive()) {
            return null;
        }

        // TODO: Do not instantiate inside method, return value from get start?
        return new Date(time.getStart());
    }

    /**
     * Clock in project at a given date and time.
     *
     * @param date Date and time for when to clock in the project.
     * @throws ClockActivityException If the project is already active.
     * @return The clocked in Time.
     */
    @NonNull
    public Time clockInAt(@NonNull Date date) throws DomainException {
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
     * Clock out project at a given date and time.
     *
     * @param date Date and time for when to clock out the project.
     * @throws ClockActivityException If the project is not active.
     * @return The clocked out Time.
     */
    @NonNull
    public Time clockOutAt(@NonNull Date date) throws DomainException {
        // Retrieve the active Time domain object, and clock
        // out with the supplied date.
        //
        // If none is available, i.e. we have not clocked in,
        // we can't clock out.
        Time time = getActiveTime();
        if (null == time || !time.isActive()) {
            throw new ClockActivityException("Unable to clock out, project is not active");
        }

        time.clockOutAt(date);
        return time;
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
        if (null != time) {
            active = time.isActive();
        }

        return active;
    }
}
