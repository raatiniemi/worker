package me.raatiniemi.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.util.NullUtil.nonNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class NullUtilTest {
    @Test
    public void nonNull_withNull() {
        assertFalse(nonNull(null));
    }

    @Test
    public void nonNull_withNonNull() {
        assertTrue(nonNull(""));
    }

    @Test
    public void isNull_withNull() {
        assertTrue(isNull(null));
    }

    @Test
    public void isNull_withNonNull() {
        assertFalse(isNull(""));
    }
}
