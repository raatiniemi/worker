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

package me.raatiniemi.worker.domain.interactor;

/**
 * Restore operation.
 */
public class RestoreBackup {
    /**
     * Strategy for restoring a backup.
     */
    private final RestoreStrategy strategy;

    /**
     * Constructor.
     *
     * @param strategy Strategy for restoring a backup.
     */
    public RestoreBackup(RestoreStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Restore backup via the strategy.
     */
    public void execute() {
        // Execute the restoration strategy.
        strategy.execute();
    }
}
