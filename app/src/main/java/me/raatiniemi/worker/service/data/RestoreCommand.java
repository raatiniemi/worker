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

/**
 * Restore operation.
 */
class RestoreCommand {
    /**
     * Strategy for restoring a backup.
     */
    private final RestoreStrategy mStrategy;

    /**
     * Constructor.
     *
     * @param strategy Strategy for restoring a backup.
     */
    RestoreCommand(RestoreStrategy strategy) {
        mStrategy = strategy;
    }

    /**
     * Get the restoration strategy.
     *
     * @return Restoration strategy.
     */
    protected RestoreStrategy getStrategy() {
        return mStrategy;
    }

    /**
     * Restore backup via the strategy.
     */
    synchronized void execute() {
        // Execute the restoration strategy.
        getStrategy().execute();
    }
}
