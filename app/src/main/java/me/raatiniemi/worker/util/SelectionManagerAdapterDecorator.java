/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.util;

import androidx.recyclerview.widget.RecyclerView;

public class SelectionManagerAdapterDecorator<T> extends SelectionManager<T> {
    private final RecyclerView.Adapter adapter;
    private final SelectionListener selectionListener;

    public SelectionManagerAdapterDecorator(
            RecyclerView.Adapter adapter,
            SelectionListener selectionListener
    ) {
        this.adapter = adapter;
        this.selectionListener = selectionListener;
    }

    @Override
    public void selectItem(T result) {
        super.selectItem(result);

        adapter.notifyDataSetChanged();
        selectionListener.onSelect();
    }

    @Override
    public void deselectItem(T result) {
        super.deselectItem(result);

        adapter.notifyDataSetChanged();
        selectionListener.onDeselect();
    }

    @Override
    public void deselectItems() {
        super.deselectItems();

        adapter.notifyDataSetChanged();
    }
}
