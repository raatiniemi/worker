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

    public int getGroupCount() {
        return mData.size();
    }

    public TimeGroup getGroupItem(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("Group position " + groupPosition);
        }

        return mData.get(groupPosition).first;
    }

    public void addGroupItem(Groupable groupable) {
        mData.add(groupable);
    }

    public void removeGroupItem(int groupPosition) {
        mData.remove(groupPosition);
    }

    public List<TimeChild> getChildItems(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("Group position " + groupPosition);
        }

        return mData.get(groupPosition).second;
    }

    public int getChildCount(int groupPosition) {
        return getChildItems(groupPosition).size();
    }

    public TimeChild getChildItem(int groupPosition, int childPosition) {
        if (childPosition < 0 || childPosition >= getChildCount(groupPosition)) {
            throw new IndexOutOfBoundsException("Child position " + childPosition);
        }

        return getChildItems(groupPosition).get(childPosition);
    }

    public void removeChildItem(int groupPosition, int childPosition) {
        getChildItems(groupPosition).remove(childPosition);

        if (0 == getChildCount(groupPosition)) {
            removeGroupItem(groupPosition);
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
