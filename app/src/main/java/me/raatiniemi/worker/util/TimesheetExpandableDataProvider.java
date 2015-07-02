package me.raatiniemi.worker.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.model.time.Time;

public class TimesheetExpandableDataProvider {
    private List<Groupable> mData;

    public TimesheetExpandableDataProvider() {
        // TODO: Instead of initializing the class field, check null value in methods.
        mData = new ArrayList<>();
    }

    public int getCount() {
        return mData.size();
    }

    public int getCount(int group) {
        return getItems(group).size();
    }

    public List<TimeChild> getItems(int group) {
        if (group < 0 || group >= getCount()) {
            throw new IndexOutOfBoundsException("Group position " + group);
        }

        return mData.get(group).getItems();
    }

    public void set(List<Groupable> data) {
        mData = data;
    }

    public TimeGroup get(int group) {
        if (group < 0 || group >= getCount()) {
            throw new IndexOutOfBoundsException("Group position " + group);
        }

        return mData.get(group).getHeader();
    }

    public TimeChild get(int group, int child) {
        if (child < 0 || child >= getCount(group)) {
            throw new IndexOutOfBoundsException("Child position " + child);
        }

        return getItems(group).get(child);
    }

    public void add(Groupable groupable) {
        mData.add(groupable);
    }

    public void remove(int group) {
        mData.remove(group);
    }

    public void remove(int group, int child) {
        getItems(group).remove(child);

        if (0 == getCount(group)) {
            remove(group);
        }
    }

    public static class Groupable {
        private TimeGroup mHeader;

        private List<TimeChild> mItems;

        public Groupable(TimeGroup header, List<TimeChild> items) {
            mHeader = header;
            mItems = items;
        }

        public TimeGroup getHeader() {
            return mHeader;
        }

        public List<TimeChild> getItems() {
            return mItems;
        }
    }

    public static abstract class Data<T> {
        private long mId;

        private T mData;

        public Data(long id, T data) {
            mId = id;
            mData = data;
        }

        public long getId() {
            return mId;
        }

        public T getData() {
            return mData;
        }
    }

    public static class TimeGroup extends Data<Date> {
        public TimeGroup(long id, Date date) {
            super(id, date);
        }

        public long getGroupId() {
            return getId();
        }

        public Date getDate() {
            return getData();
        }
    }

    public static class TimeChild extends Data<Time> {
        public TimeChild(long id, Time time) {
            super(id, time);
        }

        public long getChildId() {
            return getId();
        }

        public Time getTime() {
            return getData();
        }
    }
}
