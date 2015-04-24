package me.raatiniemi.worker.base.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

abstract public class ListAdapter<T extends List, V extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<V>
{
    private Context mContext;

    private T mItems;

    protected Context getContext()
    {
        return mContext;
    }

    public ListAdapter(Context context)
    {
        mContext = context;
    }

    public void setItems(T items)
    {
        mItems = items;
    }

    public T getItems()
    {
        return mItems;
    }
}
