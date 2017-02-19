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

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.RobolectricTestCase;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class SimpleListAdapterTest extends RobolectricTestCase {
    @Test
    public void getItemCount_withoutItems() {
        Adapter<String> adapter = new Adapter<>();

        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void getItemCount_withItems() {
        Adapter<String> adapter = new Adapter<>();

        adapter.add("Item");
        adapter.add("Item");
        adapter.add("Item");

        assertEquals(3, adapter.getItemCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_withoutItems() {
        Adapter<String> adapter = new Adapter<>();

        adapter.get(1);
    }

    @Test
    public void get_withItem() {
        Adapter<String> adapter = new Adapter<>();

        adapter.add("Item");

        assertEquals("Item", adapter.get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void set_withoutItems() {
        Adapter<String> adapter = new Adapter<>();

        adapter.set(1, "Item");
    }

    @Test
    public void set_withItem() {
        Adapter<String> adapter = new Adapter<>();

        adapter.add("Item");

        assertEquals("Item", adapter.get(0));
        adapter.set(0, "Item 1");
        assertEquals("Item 1", adapter.get(0));
    }

    @Test
    public void add_item() {
        Adapter<String> adapter = new Adapter<>();

        adapter.add("Item");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void add_items() {
        Adapter<String> adapter = new Adapter<>();

        List<String> items = new ArrayList<>();
        items.add("Item");
        items.add("Item");
        adapter.add(items);

        assertEquals(2, adapter.getItemCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void remove_withoutItems() {
        Adapter<String> adapter = new Adapter<>();

        adapter.remove(1);
    }

    @Test
    public void remove_withItem() {
        Adapter<String> adapter = new Adapter<>();

        adapter.add("Item");

        assertEquals("Item", adapter.remove(0));
    }

    @Test
    public void clear() {
        Adapter<String> adapter = new Adapter<>();

        adapter.add("Item");
        adapter.add("Item");

        assertEquals(2, adapter.getItemCount());
        adapter.clear();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void getOnItemClickListener_withoutListener() {
        Adapter<String> adapter = new Adapter<>();

        assertNull(adapter.getOnItemClickListener());
    }

    @Test
    public void getOnItemClickListener_withListener() {
        Adapter<String> adapter = new Adapter<>();

        SimpleListAdapter.OnItemClickListener listener = view -> {
        };
        adapter.setOnItemClickListener(listener);

        assertEquals(listener, adapter.getOnItemClickListener());
    }

    private class Adapter<T> extends SimpleListAdapter<T, ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }
    }
}
