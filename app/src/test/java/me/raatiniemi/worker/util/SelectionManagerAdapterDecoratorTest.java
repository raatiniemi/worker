/*
 * Copyright (C) 2017 Worker Project
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

import android.support.v7.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;

import me.raatiniemi.worker.RobolectricTestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SelectionManagerAdapterDecoratorTest extends RobolectricTestCase {
    private RecyclerView.Adapter adapter;
    private SelectionListener selectionListener;

    private SelectionManager<String> selectionManager;

    @Before
    public void setUp() {
        adapter = mock(RecyclerView.Adapter.class);
        selectionListener = mock(SelectionListener.class);

        selectionManager = new SelectionManagerAdapterDecorator<>(
                adapter,
                selectionListener
        );
    }

    @Test
    public void selectItem() {
        selectionManager.selectItem("selectItem");

        verify(adapter).notifyDataSetChanged();
        verify(selectionListener).onSelect();
    }

    @Test
    public void deselectItem() {
        selectionManager.deselectItem("deselectItem");

        verify(adapter).notifyDataSetChanged();
        verify(selectionListener).onDeselect();
    }

    @Test
    public void deselectItems() {
        selectionManager.deselectItems();

        verify(adapter).notifyDataSetChanged();
        verify(selectionListener, never()).onDeselect();
    }
}
