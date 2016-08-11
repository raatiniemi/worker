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

package me.raatiniemi.worker.presentation.base.view.activity;

import me.raatiniemi.worker.presentation.base.presenter.MvpPresenter;
import me.raatiniemi.worker.presentation.base.view.MvpView;

/**
 * Base for the model-view-presenter activity.
 *
 * @param <P> Presenter used with the activity.
 */
public abstract class MvpActivity<P extends MvpPresenter> extends BaseActivity implements MvpView {
    /**
     * Instance for the presenter.
     */
    private P presenter;

    /**
     * Create the instance for the presenter.
     *
     * @return Instance for the presenter.
     */
    protected abstract P createPresenter();

    /**
     * Retrieve the presenter, create instance if none is available.
     *
     * @return Instance for the presenter.
     */
    protected P getPresenter() {
        if (null == presenter) {
            presenter = createPresenter();
        }
        return presenter;
    }

    /**
     * Handles clean up when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // If the presenter is still active, we have to detach it.
        if (null != getPresenter()) {
            getPresenter().detachView();
        }
    }
}
