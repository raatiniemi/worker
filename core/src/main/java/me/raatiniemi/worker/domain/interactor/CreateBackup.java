/*
 * Copyright (C) 2017 Worker Project
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
 * Backup operation.
 */
public class CreateBackup {
    /**
     * Strategy for creating a backup.
     */
    private final BackupStrategy strategy;

    /**
     * Constructor.
     *
     * @param strategy Strategy for creating a backup.
     */
    public CreateBackup(BackupStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Create backup via the strategy.
     */
    public void execute() {
        // Execute the backup strategy.
        strategy.execute();
    }
}
