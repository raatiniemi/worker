package me.raatiniemi.worker.mapper;

import me.raatiniemi.worker.application.Worker;

/**
 * Registry for classes related to data mapping.
 */
final public class MapperRegistry {
    /**
     * Instance for the mapper registry.
     */
    private static MapperRegistry mInstance;

    /**
     * Instance for project mapper.
     */
    private ProjectMapper mProjectMapper;

    /**
     * Instance for the time mapper.
     */
    private TimeMapper mTimeMapper;

    /**
     * Retrieve the instance for the mapper registry.
     *
     * @return Mapper registry instance.
     */
    private static MapperRegistry getInstance() {
        if (null == mInstance) {
            mInstance = new MapperRegistry();
        }

        return mInstance;
    }

    /**
     * Retrieve the instance for the project mapper.
     *
     * @return Project mapper instance.
     */
    public static ProjectMapper getProjectMapper() {
        MapperRegistry instance = getInstance();

        if (null == instance.mProjectMapper) {
            instance.mProjectMapper = new ProjectMapper(
                Worker.getContext(),
                MapperRegistry.getTimeMapper()
            );
        }

        return instance.mProjectMapper;
    }

    /**
     * Retrieve the instance for the time mapper.
     *
     * @return Time mapper instance.
     */
    public static TimeMapper getTimeMapper() {
        MapperRegistry instance = getInstance();

        if (null == instance.mTimeMapper) {
            instance.mTimeMapper = new TimeMapper(
                Worker.getContext()
            );
        }

        return instance.mTimeMapper;
    }
}
