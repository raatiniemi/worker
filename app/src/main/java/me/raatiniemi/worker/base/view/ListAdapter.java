package me.raatiniemi.worker.base.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

abstract public class ListAdapter<T extends List, V extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<V>
{
    private Context mContext;

    /**
     * Data items for the adapter to display.
     */
    private T mItems;

    protected Context getContext()
    {
        return mContext;
    }

    public ListAdapter(Context context)
    {
        mContext = context;
    }

    /**
     * Set the data items for the adapter.
     * @param items Data items for the adapter.
     */
    public void setItems(T items)
    {
        mItems = items;
    }

    /**
     * Retrieve the data items for the adapter.
     * @return Data items for the adapter.
     */
    public T getItems()
    {
        return mItems;
    }

    /**
     * Retrieve the number of items within the data container.
     * @return Number of items within the data container.
     */
    @Override
    public int getItemCount()
    {
        return null != getItems() ? getItems().size() : 0;
    }
}
