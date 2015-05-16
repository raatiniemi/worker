package me.raatiniemi.worker.base.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

abstract public class ListAdapter<T, C extends List<T>, V extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<V> {
    private Context mContext;

    /**
     * Data items for the adapter to display.
     */
    private C mItems;

    public ListAdapter(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }

    /**
     * Retrieve the data items for the adapter.
     *
     * @return Data items for the adapter.
     */
    public C getItems() {
        return mItems;
    }

    /**
     * Set the data items for the adapter.
     *
     * @param items Data items for the adapter.
     */
    public void setItems(C items) {
        mItems = items;
    }

    /**
     * Retrieve the number of items within the data container.
     *
     * @return Number of items within the data container.
     */
    @Override
    public int getItemCount() {
        return null != getItems() ? getItems().size() : 0;
    }

    public T get(int position) {
        return getItems().get(position);
    }
}
