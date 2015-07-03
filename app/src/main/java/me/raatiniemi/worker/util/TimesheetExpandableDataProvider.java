package me.raatiniemi.worker.util;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.project.timesheet.TimesheetAdapter;

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

    public List<TimesheetAdapter.TimeChild> getItems(int group) {
        if (group < 0 || group >= getCount()) {
            throw new IndexOutOfBoundsException("Group position " + group);
        }

        return mData.get(group).getItems();
    }

    public void set(List<Groupable> data) {
        mData = data;
    }

    public TimesheetAdapter.TimeGroup get(int group) {
        if (group < 0 || group >= getCount()) {
            throw new IndexOutOfBoundsException("Group position " + group);
        }

        return mData.get(group).getHeader();
    }

    public TimesheetAdapter.TimeChild get(int group, int child) {
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
        private TimesheetAdapter.TimeGroup mHeader;

        private List<TimesheetAdapter.TimeChild> mItems;

        public Groupable(TimesheetAdapter.TimeGroup header, List<TimesheetAdapter.TimeChild> items) {
            mHeader = header;
            mItems = items;
        }

        public TimesheetAdapter.TimeGroup getHeader() {
            return mHeader;
        }

        public List<TimesheetAdapter.TimeChild> getItems() {
            return mItems;
        }
    }

}
