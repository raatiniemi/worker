package me.raatiniemi.worker.exception.domain;

import me.raatiniemi.worker.exception.DomainException;

public class ClockActivityException extends DomainException
{
    public ClockActivityException(String message)
    {
        super(message);
    }

    public ClockActivityException()
    {
        super();
    }
}
