package me.raatiniemi.worker.factory;

import android.support.annotation.NonNull;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

public class TimeFactory extends Time.Builder {
    private TimeFactory(long projectId) {
        super(projectId);
    }

    @NonNull
    public static TimeFactory builder(Long projectId) {
        return new TimeFactory(projectId);
    }

    @NonNull
    public static TimeFactory builder() {
        return builder(1L);
    }

    @Override
    public TimeFactory id(Long id) {
        super.id(id);

        return this;
    }

    @Override
    public TimeFactory startInMilliseconds(long startInMilliseconds) {
        super.startInMilliseconds(startInMilliseconds);

        return this;
    }

    @Override
    public TimeFactory stopInMilliseconds(long stopInMilliseconds) {
        super.stopInMilliseconds(stopInMilliseconds);

        return this;
    }

    @Override
    public TimeFactory register() {
        super.register();

        return this;
    }

    @Override
    public Time build() {
        try {
            return super.build();
        } catch (ClockOutBeforeClockInException e) {
            throw new RuntimeException(e);
        }
    }
}
