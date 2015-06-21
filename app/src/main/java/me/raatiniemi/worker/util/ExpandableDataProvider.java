package me.raatiniemi.worker.util;

import android.util.Pair;

import java.util.List;

public abstract class ExpandableDataProvider {
    public abstract int getGroupCount();

    public abstract Group getGroupItem(int groupPosition);

    public abstract List<Child> getChildItems(int groupPosition);

    public abstract int getChildCount(int groupPosition);

    public abstract Child getChildItem(int groupPosition, int childPosition);

    public static class Groupable extends Pair<Group, List<Child>> {
        public Groupable(Group group, List<Child> children) {
            super(group, children);
        }
    }

    public static abstract class Data {
        private int mId;

        public Data(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }

    public static abstract class Group extends Data {
        public Group(int id) {
            super(id);
        }

        public int getGroupId() {
            return getId();
        }
    }

    public static abstract class Child extends Data {
        public Child(int id) {
            super(id);
        }

        public int getChildId() {
            return getId();
        }
    }
}
