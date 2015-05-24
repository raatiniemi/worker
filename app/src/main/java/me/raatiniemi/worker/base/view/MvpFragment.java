package me.raatiniemi.worker.base.view;

import android.app.Fragment;

import me.raatiniemi.worker.base.presenter.MvpPresenter;

/**
 * Base for the model-view-presenter fragment.
 *
 * @param <P> Presenter to use with the fragment.
 * @param <T> Type of data to use with the fragment.
 */
abstract public class MvpFragment<P extends MvpPresenter, T> extends Fragment implements MvpView {
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
     * Set the data for the fragment.
     *
     * @param data Data for the fragment.
     */
    abstract public void setData(T data);

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
