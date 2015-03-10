package me.raatiniemi.worker.provider;

import android.util.Pair;

import java.util.List;

public abstract class ExpandableDataProvider
{
    public static class Groupable extends Pair<Group, List<Child>>
    {
        public Groupable(Group group, List<Child> children)
        {
            super(group, children);
        }
    }

    public static abstract class Data
    {
    }

    public static abstract class Group extends Data
    {
        public abstract int getGroupId();
    }

    public static abstract class Child extends Data
    {
        public abstract int getChildId();
    }

    public abstract int getGroupCount();

    public abstract int getChildCount(int groupPosition);

    public abstract Group getGroupItem(int groupPosition);

    public abstract Child getChildItem(int groupPosition, int childPosition);
}
