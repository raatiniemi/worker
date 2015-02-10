package me.raatiniemi.worker.exception;

public class DomainException extends Exception
{
    public DomainException(String message)
    {
        super(message);
    }

    public DomainException()
    {
        super();
    }
}
