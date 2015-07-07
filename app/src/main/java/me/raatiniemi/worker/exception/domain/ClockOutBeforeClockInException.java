package me.raatiniemi.worker.exception.domain;

import me.raatiniemi.worker.exception.DomainException;

/**
 * Exception for when clock out occur before clock in.
 */
public class ClockOutBeforeClockInException extends DomainException {
    /**
     * Constructor.
     *
     * @param message Message thrown with exception.
     */
    public ClockOutBeforeClockInException(String message) {
        super(message);
    }
}
