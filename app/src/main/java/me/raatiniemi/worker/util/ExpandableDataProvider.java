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

    public static abstract class Group<T> extends Data<T> {
        public Group(int id, T data) {
            super(id, data);
        }

        public int getGroupId() {
            return getId();
        }
    }

    public static abstract class Child<T> extends Data<T> {
        public Child(int id, T data) {
            super(id, data);
        }

        public int getChildId() {
            return getId();
        }
    }
}
