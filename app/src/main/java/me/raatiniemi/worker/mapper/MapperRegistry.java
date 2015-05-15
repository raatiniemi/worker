package me.raatiniemi.worker.mapper;

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
        if (mInstance == null) {
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

        if (instance.mProjectMapper == null) {
            instance.mProjectMapper = new ProjectMapper(MapperRegistry.getTimeMapper());
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

        if (instance.mTimeMapper == null) {
            instance.mTimeMapper = new TimeMapper();
        }

        return instance.mTimeMapper;
    }
}
