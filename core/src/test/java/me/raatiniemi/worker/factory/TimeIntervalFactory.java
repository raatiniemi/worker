package me.raatiniemi.worker.factory;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.TimeInterval;

public class TimeIntervalFactory extends TimeInterval.Builder {
    private TimeIntervalFactory(long projectId) {
        super(projectId);
    }

    public static TimeIntervalFactory builder(Long projectId) {
        return new TimeIntervalFactory(projectId);
    }

    public static TimeIntervalFactory builder() {
        return builder(1L);
    }

    @Override
    public TimeIntervalFactory id(Long id) {
        super.id(id);

        return this;
    }

    @Override
    public TimeIntervalFactory startInMilliseconds(long startInMilliseconds) {
        super.startInMilliseconds(startInMilliseconds);

        return this;
    }

    @Override
    public TimeIntervalFactory stopInMilliseconds(long stopInMilliseconds) {
        super.stopInMilliseconds(stopInMilliseconds);

        return this;
    }

    @Override
    public TimeIntervalFactory register() {
        super.register();

        return this;
    }

    @Override
    public TimeInterval build() {
        try {
            return super.build();
        } catch (ClockOutBeforeClockInException e) {
            throw new RuntimeException(e);
        }
    }
}
