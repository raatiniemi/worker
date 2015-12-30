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

package me.raatiniemi.worker.base.view.fragment;

import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.presenter.MvpPresenter;
import me.raatiniemi.worker.base.view.MvpView;

/**
 * Base for the model-view-presenter fragment.
 *
 * @param <P> Presenter to use with the fragment.
 * @param <T> Type of data to use with the fragment.
 */
abstract public class MvpFragment<P extends MvpPresenter, T> extends BaseFragment implements MvpView {
    /**
     * Instance for the presenter.
     */
    private P mPresenter;

    /**
     * Create the instance for the presenter.
     *
     * @return Instance for the presenter.
     */
    abstract protected P createPresenter();

    /**
     * Retrieve the presenter, create instance if none is available.
     *
     * @return Instance for the presenter.
     */
    protected P getPresenter() {
        if (null == mPresenter) {
            mPresenter = createPresenter();
        }
        return mPresenter;
    }

    /**
     * Get the data for the fragment.
     *
     * @return Data for the fragment.
     */
    abstract public T getData();

    /**
     * Set the data for the fragment.
     *
     * @param data Data for the fragment.
     */
    abstract public void setData(T data);

    /**
     * Display an error message to the user.
     *
     * @param e Exception has been thrown.
     */
    public void showError(Throwable e) {
        // Check if the exception message have been populated,
        // otherwise use the default error message.
        String message = e.getMessage();
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.error_message_unknown);
        }

        // Display the error message.
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
        ).show();
    }

    /**
     * Handles clean up when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // If the presenter is still active, we have to detach it.
        if (null != getPresenter()) {
            getPresenter().detachView();
        }
    }
}
