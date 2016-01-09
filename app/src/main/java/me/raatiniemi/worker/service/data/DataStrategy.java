/*
 * Copyright (C) 2015-2016 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.service.data;

import android.content.Context;

import de.greenrobot.event.EventBus;

/**
 * Base for performing data operations.
 * <p>
 * TODO: Only use the event bus instead of notification manager?
 * By only using the event bus the testability of the data strategy is improved,
 * it would also mean less dependencies to handle.
 * <p>
 * The notification manager can be invoked where the event is handled, provided
 * that a single handler receives the event (avoid multiple handlers).
 * <p>
 * TODO: Inject the external storage handler via constructor.
 */
abstract class DataStrategy {
    /**
     * Context used with the data operation.
     */
    private Context mContext;

    /**
     * Event bus used for notification.
     */
    private EventBus mEventBus;

    /**
     * Constructor.
     *
     * @param context  Context used with the data operation.
     * @param eventBus Event bus used for notification.
     */
    DataStrategy(Context context, EventBus eventBus) {
        mContext = context;
        mEventBus = eventBus;
    }

    /**
     * Get the context used with the data operation.
     *
     * @return Context used with the data operation.
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * Get the event bus used for notification.
     *
     * @return Event bus used for notification.
     */
    protected EventBus getEventBus() {
        return mEventBus;
    }

    /**
     * Execute data operation.
     */
    abstract void execute();
}
