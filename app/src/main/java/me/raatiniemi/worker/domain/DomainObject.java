package me.raatiniemi.worker.domain;

public class DomainObject
{
    private Long mId;

    public DomainObject(Long id)
    {
        mId = id;
    }

    public Long getId()
    {
        return mId;
    }
}
