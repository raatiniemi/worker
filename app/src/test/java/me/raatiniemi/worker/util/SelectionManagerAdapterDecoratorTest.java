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

import org.junit.Before;
import org.junit.Test;

import androidx.recyclerview.widget.RecyclerView;
import me.raatiniemi.worker.RobolectricTestCase;
import me.raatiniemi.worker.features.shared.view.adapter.EmptyRecyclerViewAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SelectionManagerAdapterDecoratorTest extends RobolectricTestCase {
    private final RecyclerView.Adapter adapter = new EmptyRecyclerViewAdapter();
    private SelectionListener selectionListener;

    private SelectionManager<String> selectionManager;

    @Before
    public void setUp() {
        selectionListener = mock(SelectionListener.class);

        selectionManager = new SelectionManagerAdapterDecorator<>(
                adapter,
                selectionListener
        );
    }

    @Test
    public void selectItem() {
        selectionManager.selectItem("selectItem");

        verify(selectionListener).onSelect();
    }

    @Test
    public void deselectItem() {
        selectionManager.deselectItem("deselectItem");

        verify(selectionListener).onDeselect();
    }

    @Test
    public void deselectItems() {
        selectionManager.deselectItems();

        verify(selectionListener, never()).onDeselect();
    }
}
