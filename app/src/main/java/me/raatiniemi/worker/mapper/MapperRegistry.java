package me.raatiniemi.worker.mapper;

final public class MapperRegistry
{
    private ProjectMapper mProjectMapper;

    private TimeMapper mTimeMapper;

    private static MapperRegistry mInstance;

    private static MapperRegistry getInstance()
    {
        if (mInstance == null) {
            mInstance = new MapperRegistry();
        }
        return mInstance;
    }

    public static ProjectMapper getProjectMapper()
    {
        MapperRegistry instance = getInstance();

        if (instance.mProjectMapper == null) {
            instance.mProjectMapper = new ProjectMapper(MapperRegistry.getTimeMapper());
        }

        return instance.mProjectMapper;
    }

    public static TimeMapper getTimeMapper()
    {
        MapperRegistry instance = getInstance();

        if (instance.mTimeMapper == null) {
            instance.mTimeMapper = new TimeMapper();
        }

        return instance.mTimeMapper;
    }
}
