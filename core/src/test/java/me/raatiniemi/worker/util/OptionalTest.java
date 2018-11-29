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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.NoSuchElementException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class OptionalTest {
    @Test
    public void empty() {
        Optional<String> value = Optional.empty();

        assertFalse(value.isPresent());
    }

    @Test
    public void of_withValue() {
        Optional<String> value = Optional.of("Value");

        assertTrue(value.isPresent());
        assertEquals("Value", value.get());
    }

    @Test(expected = NullPointerException.class)
    public void of_withNull() {
        Optional.of(null);
    }

    @Test
    public void ofNullable_withValue() {
        Optional<String> value = Optional.ofNullable("Value");

        assertTrue(value.isPresent());
        assertEquals("Value", value.get());
    }

    @Test
    public void ofNullable_withNull() {
        Optional<String> value = Optional.ofNullable(null);

        assertFalse(value.isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void get_withNull() {
        Optional<String> value = Optional.empty();

        value.get();
    }

    @Test
    public void orElse_withValue() {
        Optional<String> value = Optional.of("Value");

        assertTrue(value.isPresent());
        assertEquals("Value", value.orElse("orElse"));
    }

    @Test
    public void orElse_withoutValue() {
        Optional<String> value = Optional.empty();

        assertFalse(value.isPresent());
        assertEquals("orElse", value.orElse("orElse"));
    }

    @Test(expected = NullPointerException.class)
    public void orElse_withNull() {
        Optional<String> value = Optional.empty();

        value.orElse(null);
    }
}
