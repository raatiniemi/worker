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

package me.raatiniemi.worker.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DomainObjectTest {
    @Test
    public void getId_defaultValue() {
        DomainObject object = new DomainObject() {
        };

        assertNull(object.getId());
    }

    @Test
    public void getId_valueFromConstructor() {
        DomainObject object = new DomainObject(1L) {
        };

        assertEquals(Long.valueOf(1L), object.getId());
    }

    @Test
    public void getId_valueFromSetter() {
        DomainObject object = new DomainObject() {
        };
        object.setId(1L);

        assertEquals(Long.valueOf(1L), object.getId());
    }
}
