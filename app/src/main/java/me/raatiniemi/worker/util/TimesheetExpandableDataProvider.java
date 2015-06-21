package me.raatiniemi.worker.util;

import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.model.time.Time;

public class TimesheetExpandableDataProvider extends ExpandableDataProvider {
    private List<Groupable> mData;

    public TimesheetExpandableDataProvider(List<Groupable> data) {
        mData = data;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public Group getGroupItem(int groupPosition) {
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

    @Override
    public List<Child> getChildItems(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("Group position " + groupPosition);
        }

        return mData.get(groupPosition).second;
    }

    @Override
    public int getChildCount(int groupPosition) {
        return getChildItems(groupPosition).size();
    }

    @Override
    public Child getChildItem(int groupPosition, int childPosition) {
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

    public static class TimeGroup extends Group<Date> {
        public TimeGroup(int id, Date date) {
            super(id, date);
        }

        public Date getDate() {
            return getData();
        }
    }

    public static class TimeChild extends Child<Time> {
        public TimeChild(int id, Time time) {
            super(id, time);
        }

        public Time getTime() {
            return getData();
        }
    }
}
