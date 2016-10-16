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

package me.raatiniemi.worker.presentation.projects.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.presenter.NewProjectPresenter;
import me.raatiniemi.worker.presentation.util.Keyboard;

public class NewProjectFragment extends DialogFragment implements NewProjectView, DialogInterface.OnShowListener {
    private static final String TAG = "NewProjectFragment";

    @BindView(R.id.fragment_new_project_name)
    EditText projectName;

    @Inject
    NewProjectPresenter presenter;

    private Unbinder unbinder;

    /**
     * Callback handler for the "OnCreateProjectListener".
     */
    private OnCreateProjectListener onCreateProjectListener;

    public static NewProjectFragment newFragment(@NonNull OnCreateProjectListener onCreateProjectListener) {
        NewProjectFragment fragment = new NewProjectFragment();
        fragment.onCreateProjectListener = onCreateProjectListener;

        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setup();
    }

    /**
     * TODO: Remove method call when `minSdkVersion` is +23.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setup();
        }
    }

    private void setup() {
        // Check that we actually have a listener available, otherwise we
        // should not attempt to create new projects.
        if (null == onCreateProjectListener) {
            // The real reason for failure is to technical to display to the
            // user, hence the unknown error message.
            //
            // And, the listener should always be available in the production
            // version, i.e. this should just be seen as developer feedback.
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.error_message_unknown,
                    Snackbar.LENGTH_SHORT
            ).show();

            Log.w(TAG, "No OnCreateProjectListener have been supplied");
            dismiss();

            return;
        }

        ((Worker) getActivity().getApplication()).getProjectsComponent()
                .inject(this);

        presenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_project, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle(R.string.fragment_new_project_title);
        getDialog().setOnShowListener(this);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        // We might have dismissed the dialog, we have to make sure that the
        // dialog and activity are still available before we can continue.
        if (null == dialog || null == getActivity()) {
            Log.d(TAG, "No dialog/activity available, exiting...");
            return;
        }

        // Force the keyboard to show when the dialog is showing.
        Keyboard.show(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        presenter.detachView();
    }

    @OnClick(R.id.fragment_new_project_create)
    void createProject() {
        String projectName = this.projectName.getText().toString();
        presenter.createNewProject(projectName);
    }

    @OnClick(R.id.fragment_new_project_cancel)
    void dismissDialog() {
        dismiss();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void createProjectSuccessful(final Project project) {
        onCreateProjectListener.accept(project);

        dismiss();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showInvalidNameError() {
        projectName.setError(getString(R.string.error_message_project_name_missing));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showDuplicateNameError() {
        projectName.setError(getString(R.string.error_message_project_name_already_exists));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showUnknownError() {
        projectName.setError(getString(R.string.error_message_unknown));
    }

    @FunctionalInterface
    public interface OnCreateProjectListener {
        void accept(Project project);
    }
}
