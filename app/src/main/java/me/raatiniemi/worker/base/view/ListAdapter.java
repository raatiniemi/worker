package me.raatiniemi.worker.base.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Base adapter for working with the RecyclerView.
 *
 * @param <T> Reference type for a single item within the list collection.
 * @param <V> Reference type for the view holder.
 */
abstract public class ListAdapter<T, V extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<V> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "ListAdapter";

    /**
     * Context to use.
     */
    private Context mContext;

    /**
     * Data items for the adapter to display.
     */
    private List<T> mItems;

    /**
     * On click listener for list items.
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * On click listener for views within the ListAdapter.
     */
    private OnClickListener mOnClickListener = new OnClickListener();

    /**
     * Construct the ListAdapter.
     *
     * @param context Context to use.
     */
    public ListAdapter(Context context) {
        mContext = context;
    }

    /**
     * Retrieve the context to use.
     *
     * @return Context to use.
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * Get the items from the adapter.
     *
     * @return Items from the adapter.
     */
    public List<T> getItems() {
        if (null == mItems) {
            mItems = new ArrayList<>();
        }
        return mItems;
    }

    /**
     * Set items for the adapter.
     *
     * @param items Items for the adapter.
     */
    public void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    /**
     * Retrieve the number of items.
     *
     * @return Number of items.
     */
    @Override
    public int getItemCount() {
        return getItems().size();
    }

    /**
     * Get item from the data container.
     *
     * @param position Position of the item to retrieve.
     * @return Item at the supplied position.
     */
    public T get(int position) {
        return getItems().get(position);
    }

    /**
     * Update item at a given position within the data container.
     *
     * @param position Position of the item to update.
     * @param item Item to update the data container position.
     */
    public void set(int position, T item) {
        getItems().set(position, item);

        // Notify the adapter that data item have changed.
        notifyItemChanged(position);
    }

    /**
     * Add an item to the data container.
     *
     * @param item Item to add to the data container.
     * @return Position of the new item within the container.fa
     */
    public int add(T item) {
        // Retrieve the position for the new item by retrieving the number of
        // items within the container before adding the new item.
        int position = getItemCount();
        getItems().add(item);

        // Notify the adapter a new item have been added.
        notifyItemInserted(position);

        // Return the position for the new item.
        return position;
    }

    /**
     * Add collection of items to the adapter.
     *
     * @param items Items to add to the adapter.
     */
    public int add(List<T> items) {
        int position = getItemCount();
        getItems().addAll(items);

        notifyItemRangeInserted(position, items.size());

        return position;
    }

    /**
     * Retrieve the click listener for list items.
     *
     * @return Click listener for list items, or null if none has been supplied.
     */
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    /**
     * Set the click listener for list items.
     *
     * @param onItemClickListener Click listener for list items.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Retrieve the click listener for list item views.
     *
     * @return Click listener for list item views.
     */
    protected OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    /**
     * Click listener interface for list items.
     */
    public interface OnItemClickListener {
        /**
         * Handles list item click events.
         *
         * @param view View that has been clicked.
         */
        void onItemClick(View view);
    }

    /**
     * On click listener for list items.
     */
    protected class OnClickListener implements View.OnClickListener {
        /**
         * Handles click events to the list item.
         *
         * @param view List item that have been clicked.
         */
        @Override
        public void onClick(View view) {
            // Check that the OnItemClickListener have been supplied.
            if (null == getOnItemClickListener()) {
                Log.e(TAG, "No OnItemClickListener have been supplied");
                return;
            }

            // Relay the event with the item view to the OnItemClickListener.
            getOnItemClickListener().onItemClick(view);
        }
    }
}
