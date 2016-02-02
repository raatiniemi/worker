/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.presenter;

import android.content.Context;

import me.raatiniemi.worker.presentation.base.presenter.RxPresenter;
import me.raatiniemi.worker.presentation.view.NewProjectView;
import me.raatiniemi.worker.presentation.view.fragment.NewProjectFragment;

/**
 * Presenter for the {@link NewProjectFragment}.
 */
public class NewProjectPresenter extends RxPresenter<NewProjectView> {
    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     */
    public NewProjectPresenter(final Context context) {
        super(context);
    }
}
