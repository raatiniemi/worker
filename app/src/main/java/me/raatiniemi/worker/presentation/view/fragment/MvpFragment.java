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

package me.raatiniemi.worker.presentation.view.fragment;

import me.raatiniemi.worker.presentation.presenter.MvpPresenter;

/**
 * Base for the model-view-presenter fragment.
 *
 * @param <P> Presenter to use with the fragment.
 */
public abstract class MvpFragment<P extends MvpPresenter> extends BaseFragment {
    protected abstract P getPresenter();
}
