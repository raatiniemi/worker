/*
 * Copyright (C) 2015-2016 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.presentation.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.raatiniemi.util.NullUtil.isNull;

/**
 * Base adapter for working with the RecyclerView.
 *
 * @param <T> Reference type for a single item within the list collection.
 * @param <V> Reference type for the view holder.
 */
public abstract class SimpleListAdapter<T, V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V>
        implements ListAdapter<T> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "SimpleListAdapter";

    /**
     * Context used with the adapter.
     */
    private final Context context;

    /**
     * On click listener for views.
     */
    private final OnClickListener onClickListener = new OnClickListener();

    /**
     * Available items.
     */
    private final List<T> items = new ArrayList<>();

    /**
     * On click listener for items.
     */
    private OnItemClickListener onItemClickListener;

    /**
     * Constructor.
     *
     * @param context Context used with the adapter.
     */
    public SimpleListAdapter(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Get the adapter context.
     *
     * @return Adapter context.
     */
    @NonNull
    protected Context getContext() {
        return context;
    }

    /**
     * Get the number of items within the adapter.
     *
     * @return Number of items within the adapter.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Check whether an index exists.
     *
     * @param index Index to check.
     * @return True if index exists, otherwise false.
     */
    private boolean has(int index) {
        // Check if index is within bounds.
        return 0 <= index && getItemCount() > index;
    }

    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    public T get(int index) {
        if (!has(index)) {
            throwIndexOutOfBounds(index);
        }

        return items.get(index);
    }

    private void throwIndexOutOfBounds(int index) {
        throw new IndexOutOfBoundsException(
                "Invalid index " + index + ", size is " + getItemCount()
        );
    }

    /**
     * @inheritDoc
     */
    @Override
    public void set(int index, @NonNull T item) {
        if (!has(index)) {
            throwIndexOutOfBounds(index);
        }

        // Update the item and notify the adapter.
        items.set(index, item);
        notifyItemChanged(index);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(@NonNull T item) {
        // Retrieve the index for the new item by retrieving the number of
        // items within the adapter before adding the new item.
        int index = getItemCount();
        items.add(item);

        // Notify the adapter a new item have been added.
        notifyItemInserted(index);

        // Return the index for the new item.
        return index;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(final int index, @NonNull final T item) {
        if (getItemCount() == index) {
            add(item);
            return;
        }

        if (!has(index)) {
            throwIndexOutOfBounds(index);
        }

        // Add the item at the specified index.
        items.add(index, item);

        // Notify the adapter a new item have been added.
        notifyItemInserted(index);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int add(@NonNull List<T> items) {
        // Retrieve the current count to have a reference point
        // at which location the new items will be inserted.
        int index = getItemCount();
        this.items.addAll(items);

        // Notify and refresh the new items.
        notifyItemRangeInserted(index, items.size());

        // Return the reference point for the location of the new items.
        return index;
    }

    /**
     * @inheritDoc
     */
    @Override
    @NonNull
    public T remove(int index) {
        if (!has(index)) {
            throwIndexOutOfBounds(index);
        }

        // Remove the item from the internal data container.
        T item = items.remove(index);

        // Notify the adapter of the deletion.
        notifyItemRemoved(index);

        // Return the removed item.
        return item;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    /**
     * Retrieve the click listener for list items.
     *
     * @return Click listener for list items, or null if none has been supplied.
     */
    @Nullable
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * Set the click listener for list items.
     *
     * @param onItemClickListener Click listener for list items.
     */
    public void setOnItemClickListener(@NonNull OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Retrieve the click listener for list item views.
     *
     * @return Click listener for list item views.
     */
    @NonNull
    protected OnClickListener getOnClickListener() {
        return onClickListener;
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
        void onItemClick(@NonNull View view);
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
        public void onClick(@NonNull View view) {
            // Check that the OnItemClickListener have been supplied.
            if (isNull(getOnItemClickListener())) {
                Log.e(TAG, "No OnItemClickListener have been supplied");
                return;
            }

            // Relay the event with the item view to the OnItemClickListener.
            getOnItemClickListener().onItemClick(view);
        }
    }
}
