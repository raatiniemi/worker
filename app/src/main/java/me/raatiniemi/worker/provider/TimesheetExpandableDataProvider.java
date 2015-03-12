package me.raatiniemi.worker.provider;

import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.Time;

public class TimesheetExpandableDataProvider extends ExpandableDataProvider
{
    public static class TimeGroup extends Group
    {
        private int mId;

        private Date mDate;

        public TimeGroup(int id, Date date)
        {
            mId = id;
            mDate = date;
        }

        @Override
        public int getGroupId()
        {
            return mId;
        }

        public Date getDate()
        {
            return mDate;
        }
    }

    public static class TimeChild extends Child
    {
        private int mId;

        private Time mTime;

        public TimeChild(int id, Time time)
        {
            mId = id;
            mTime = time;
        }

        @Override
        public int getChildId()
        {
            return mId;
        }

        public Time getTime()
        {
            return mTime;
        }
    }

    private List<Groupable> mData;

    public TimesheetExpandableDataProvider(List<Groupable> data)
    {
        mData = data;
    }

    @Override
    public int getGroupCount()
    {
        return mData.size();
    }

    @Override
    public Group getGroupItem(int groupPosition)
    {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("Group position " + groupPosition);
        }

        return mData.get(groupPosition).first;
    }

    @Override
    public List<Child> getChildItems(int groupPosition)
    {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("Group position " + groupPosition);
        }

        return mData.get(groupPosition).second;
    }

    @Override
    public int getChildCount(int groupPosition)
    {
        return getChildItems(groupPosition).size();
    }

    @Override
    public Child getChildItem(int groupPosition, int childPosition)
    {
        if (childPosition < 0 || childPosition >= getChildCount(groupPosition)) {
            throw new IndexOutOfBoundsException("Child position " + childPosition);
        }

        return getChildItems(groupPosition).get(childPosition);
    }
}
