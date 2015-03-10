package me.raatiniemi.worker.provider;

import java.util.List;

public class ExpandableTimeDataProvider extends ExpandableDataProvider
{
    public static class TimeGroup extends Group
    {
        @Override
        public int getGroupId()
        {
            return 0;
        }
    }

    public static class TimeChild extends Child
    {
        @Override
        public int getChildId()
        {
            return 0;
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
