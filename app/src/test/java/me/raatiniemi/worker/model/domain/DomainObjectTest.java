/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.model.domain;

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
        Long id = 1L;

        DomainObject object = new DomainObject(id) {
        };

        assertEquals(id, object.getId());
    }

    @Test
    public void getId_valueFromSetter() {
        Long id = 1L;

        DomainObject object = new DomainObject() {
        };
        object.setId(id);

        assertEquals(id, object.getId());
    }
}
