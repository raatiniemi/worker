/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.util;

import android.support.v7.widget.RecyclerView;

public class SelectionManagerAdapterDecorator<T> extends SelectionManager<T> {
    private RecyclerView.Adapter mAdapter;
    private SelectionListener mSelectionListener;

    public SelectionManagerAdapterDecorator(
            RecyclerView.Adapter adapter,
            SelectionListener selectionListener
    ) {
        mAdapter = adapter;
        mSelectionListener = selectionListener;
    }

    @Override
    public void selectItem(T result) {
        super.selectItem(result);

        mAdapter.notifyDataSetChanged();
        mSelectionListener.onSelect();
    }

    @Override
    public void deselectItem(T result) {
        super.deselectItem(result);

        mAdapter.notifyDataSetChanged();
        mSelectionListener.onDeselect();
    }

    @Override
    public void deselectItems() {
        super.deselectItems();

        mAdapter.notifyDataSetChanged();
    }
}
