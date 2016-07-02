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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SelectionManagerAdapterDecoratorTest {
    private RecyclerView.Adapter mAdapter;
    private SelectionListener mSelectionListener;

    private SelectionManager<String> mSelectionManager;

    @Before
    public void setUp() throws Exception {
        mAdapter = mock(RecyclerView.Adapter.class);
        mSelectionListener = mock(SelectionListener.class);

        mSelectionManager = new SelectionManagerAdapterDecorator<>(
                mAdapter,
                mSelectionListener
        );
    }

    @Test
    public void selectItem() {
        mSelectionManager.selectItem("selectItem");

        verify(mAdapter).notifyDataSetChanged();
        verify(mSelectionListener).onSelect();
    }

    @Test
    public void deselectItem() {
        mSelectionManager.deselectItem("deselectItem");

        verify(mAdapter).notifyDataSetChanged();
        verify(mSelectionListener).onDeselect();
    }

    @Test
    public void deselectItems() {
        mSelectionManager.deselectItems();

        verify(mAdapter).notifyDataSetChanged();
        verify(mSelectionListener, never()).onDeselect();
    }
}
