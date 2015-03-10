package me.raatiniemi.worker.provider;

import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.Time;

public class ExpandableTimeDataProvider extends ExpandableDataProvider
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

    public ExpandableTimeDataProvider(List<Groupable> data)
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
    public int getChildCount(int groupPosition)
    {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("Group position " + groupPosition);
        }

        return mData.get(groupPosition).second.size();
    }

    @Override
    public Child getChildItem(int groupPosition, int childPosition)
    {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("Group position " + groupPosition);
        }

        if (childPosition < 0 || childPosition >= getChildCount(groupPosition)) {
            throw new IndexOutOfBoundsException("Child position " + childPosition);
        }

        return mData.get(groupPosition).second.get(childPosition);
    }
}
