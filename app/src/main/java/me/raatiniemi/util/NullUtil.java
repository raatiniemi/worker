package me.raatiniemi.util;

public final class NullUtil {
    private NullUtil() {
    }

    /**
     * Check whether an object is not null.
     *
     * @param o Object to check for not null.
     * @return True if object is not null, otherwise false.
     */
    public static boolean nonNull(Object o) {
        return null != o;
    }

    /**
     * Check whether an object is null.
     *
     * @param o Object to check for null.
     * @return True if object is null, otherwise false.
     */
    public static boolean isNull(Object o) {
        return null == o;
    }
}
