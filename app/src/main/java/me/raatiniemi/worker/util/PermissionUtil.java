/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.util;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * Utility for common permission related operations.
 */
public class PermissionUtil {
    private PermissionUtil() {
    }

    /**
     * Check if permission have been granted.
     *
     * @param context    Context used.
     * @param permission The name of the permission being checked.
     * @return true if permission have been granted, otherwise false.
     */
    public static boolean havePermission(
            @NonNull Context context,
            @NonNull String permission
    ) {
        return ActivityCompat.checkSelfPermission(
                context,
                permission
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check that all permissions have been granted.
     *
     * @param grantResults The grant results for permissions.
     * @return true if all permissions have been granted, otherwise false.
     */
    public static boolean verifyPermissions(@NonNull int[] grantResults) {
        if (1 > grantResults.length) {
            return false;
        }

        for (int result : grantResults) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                return false;
            }
        }
        return true;
    }
}
