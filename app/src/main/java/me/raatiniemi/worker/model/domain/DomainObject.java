package me.raatiniemi.worker.model.domain;

/**
 * Base class for the domain objects.
 */
public class DomainObject {
    /**
     * Id for the domain object.
     */
    private Long mId;

    /**
     * Instantiate the domain object with id.
     *
     * @param id Id for the domain object.
     */
    public DomainObject(Long id) {
        setId(id);
    }

    /**
     * Retrieve the id for the domain object.
     *
     * @return Id for the domain object.
     */
    public Long getId() {
        return mId;
    }

    /**
     * Internal setter method for the id.
     *
     * @param id Id for the domain object.
     */
    private void setId(Long id) {
        mId = id;
    }
}
