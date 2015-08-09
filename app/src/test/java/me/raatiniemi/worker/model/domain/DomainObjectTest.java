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
    public void getId_DefaultValue_Null() {
        DomainObject object = new DomainObject() {
        };

        assertNull(object.getId());
    }

    @Test
    public void getId_ValueFromConstructor_True() {
        Long id = 1L;

        DomainObject object = new DomainObject(id) {
        };

        assertEquals(id, object.getId());
    }

    @Test
    public void getId_ValueFromSetter_True() {
        Long id = 1L;

        DomainObject object = new DomainObject() {
        };
        object.setId(id);

        assertEquals(id, object.getId());
    }
}
