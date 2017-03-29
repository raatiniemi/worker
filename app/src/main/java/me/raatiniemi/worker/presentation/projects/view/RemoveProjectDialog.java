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

package me.raatiniemi.worker.presentation.projects.view;

import android.content.Context;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.view.dialog.RxDialog;
import rx.Observable;

final class RemoveProjectDialog {
    private final static int TITLE = R.string.confirm_delete_project_title;
    private final static int MESSAGE = R.string.confirm_delete_project_message;

    @NonNull
    static Observable<Integer> show(@NonNull Context context) {
        return RxDialog.build(context, TITLE, MESSAGE);
    }
}
