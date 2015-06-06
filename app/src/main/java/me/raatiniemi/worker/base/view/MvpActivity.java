package me.raatiniemi.worker.base.view;

import me.raatiniemi.worker.base.presenter.MvpPresenter;

abstract public class MvpActivity<P extends MvpPresenter> extends BaseActivity implements MvpView {
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
