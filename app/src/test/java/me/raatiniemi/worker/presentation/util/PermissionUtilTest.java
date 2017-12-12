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

package me.raatiniemi.worker.presentation.util;

import android.content.Context;
import android.content.pm.PackageManager;

import org.junit.Test;

import me.raatiniemi.worker.RobolectricTestCase;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PermissionUtilTest extends RobolectricTestCase {
    private static final String PERMISSION = "Permission";

    @Test
    public void havePermission_granted() {
        Context context = mock(Context.class);
        when(context.checkPermission(eq(PERMISSION), anyInt(), anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        boolean havePermission = PermissionUtil.havePermission(context, PERMISSION);

        assertTrue(havePermission);
    }

    @Test
    public void havePermission_denied() {
        Context context = mock(Context.class);
        when(context.checkPermission(eq(PERMISSION), anyInt(), anyInt()))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        boolean havePermission = PermissionUtil.havePermission(context, PERMISSION);

        assertFalse(havePermission);
    }

    @Test
    public void verifyPermissions_withoutResults() {
        int[] grantResults = new int[]{};

        assertFalse(PermissionUtil.verifyPermissions(grantResults));
    }

    @Test
    public void verifyPermissions_withGrantedResult() {
        int[] grantResults = new int[]{
                PackageManager.PERMISSION_GRANTED
        };

        assertTrue(PermissionUtil.verifyPermissions(grantResults));
    }

    @Test
    public void verifyPermissions_withGrantedResults() {
        int[] grantResults = new int[]{
                PackageManager.PERMISSION_GRANTED,
                PackageManager.PERMISSION_GRANTED
        };

        assertTrue(PermissionUtil.verifyPermissions(grantResults));
    }

    @Test
    public void verifyPermissions_withMixedResults() {
        int[] grantResults = new int[]{
                PackageManager.PERMISSION_GRANTED,
                PackageManager.PERMISSION_DENIED
        };

        assertFalse(PermissionUtil.verifyPermissions(grantResults));
    }
}
