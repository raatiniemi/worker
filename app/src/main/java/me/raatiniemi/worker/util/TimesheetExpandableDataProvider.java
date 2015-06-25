package me.raatiniemi.worker.util;

import android.util.Pair;

import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.model.time.Time;

public class TimesheetExpandableDataProvider {
    private List<Groupable> mData;

    public TimesheetExpandableDataProvider(List<Groupable> data) {
        mData = data;
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

        return mData.get(group).second;
    }

    public TimeGroup get(int group) {
        if (group < 0 || group >= getCount()) {
            throw new IndexOutOfBoundsException("Group position " + group);
        }

        return mData.get(group).first;
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

    public static class Groupable extends Pair<TimeGroup, List<TimeChild>> {
        public Groupable(TimeGroup group, List<TimeChild> children) {
            super(group, children);
        }
    }

    public static abstract class Data<T> {
        private int mId;

        private T mData;

        public Data(int id, T data) {
            mId = id;
            mData = data;
        }

        public int getId() {
            return mId;
        }

        public T getData() {
            return mData;
        }
    }

    public static class TimeGroup extends Data<Date> {
        public TimeGroup(int id, Date date) {
            super(id, date);
        }

        public int getGroupId() {
            return getId();
        }

        public Date getDate() {
            return getData();
        }
    }

    public static class TimeChild extends Data<Time> {
        public TimeChild(int id, Time time) {
            super(id, time);
        }

        public int getChildId() {
            return getId();
        }

        public Time getTime() {
            return getData();
        }
    }
}
