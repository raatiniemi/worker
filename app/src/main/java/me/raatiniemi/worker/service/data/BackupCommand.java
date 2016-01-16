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

/**
 * Backup operation.
 */
class BackupCommand extends DataCommand {
    /**
     * Backup strategy.
     */
    private BackupStrategy mStrategy;

    /**
     * @inheritDoc
     */
    BackupCommand(Context context, BackupStrategy strategy) {
        super(context);

        mStrategy = strategy;
    }

    /**
     * Get the backup strategy.
     *
     * @return Backup strategy.
     */
    protected BackupStrategy getStrategy() {
        return mStrategy;
    }

    /**
     * @inheritDoc
     */
    @Override
    synchronized void execute() {
        // Execute the backup strategy.
        getStrategy().execute();
    }
}
