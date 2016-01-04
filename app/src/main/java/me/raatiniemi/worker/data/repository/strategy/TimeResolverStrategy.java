/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.data.repository.strategy;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.mapper.TimeEntityMapper;

public class TimeResolverStrategy extends ContentResolverStrategy<TimeEntityMapper> implements TimeStrategy {
    /**
     * @inheritDoc
     */
    public TimeResolverStrategy(
            @NonNull ContentResolver contentResolver,
            @NonNull TimeEntityMapper entityMapper
    ) {
        super(contentResolver, entityMapper);
    }
}
