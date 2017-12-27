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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.model.CreateProjectEvent;
import me.raatiniemi.worker.presentation.projects.viewmodel.CreateProjectViewModel;
import me.raatiniemi.worker.presentation.util.Keyboard;
import me.raatiniemi.worker.presentation.view.fragment.RxDialogFragment;
import timber.log.Timber;

import static me.raatiniemi.worker.presentation.util.RxUtil.applySchedulers;
import static me.raatiniemi.worker.util.NullUtil.isNull;

public class CreateProjectFragment extends RxDialogFragment implements DialogInterface.OnShowListener {
    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @Inject
    EventBus eventBus;

    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @Inject
    CreateProjectViewModel.ViewModel viewModel;

    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @BindView(R.id.fragment_create_project_name)
    EditText projectName;

    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @BindView(R.id.fragment_create_project_submit)
    TextView projectSubmit;

    private Unbinder unbinder;

    public static CreateProjectFragment newInstance() {
        return new CreateProjectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((WorkerApplication) getActivity().getApplication())
                .getProjectsComponent()
                .inject(this);

        viewModel.output().createProjectSuccess()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe(this::success);

        viewModel.error().invalidProjectNameError()
                .compose(bindToLifecycle())
                .subscribe(message -> showInvalidNameError());

        viewModel.error().duplicateProjectNameError()
                .compose(bindToLifecycle())
                .subscribe(message -> showDuplicateNameError());

        viewModel.error().createProjectError()
                .compose(bindToLifecycle())
                .subscribe(message -> showUnknownError());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle(R.string.fragment_create_project_title);
        getDialog().setOnShowListener(this);

        viewModel.output().isProjectNameValid()
                .compose(bindToLifecycle())
                .subscribe(projectSubmit::setEnabled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        // We might have dismissed the dialog, we have to make sure that the
        // dialog and activity are still available before we can continue.
        if (isNull(dialog) || isNull(getActivity())) {
            Timber.d("No dialog/activity available, exiting...");
            return;
        }

        // Force the keyboard to show when the dialog is showing.
        Keyboard.show(getActivity());
    }

    @OnTextChanged(R.id.fragment_create_project_name)
    void onProjectName(final CharSequence name) {
        viewModel.input().projectName(name.toString());
    }

    @OnClick(R.id.fragment_create_project_submit)
    void onCreateProject() {
        viewModel.input().createProject();
    }

    @OnClick(R.id.fragment_create_project_dismiss)
    void onDismissDialog() {
        dismiss();
    }

    private void success(Project project) {
        eventBus.post(new CreateProjectEvent(project));

        dismiss();
    }

    private void showInvalidNameError() {
        projectName.setError(getString(R.string.error_message_project_name_missing));
    }

    private void showDuplicateNameError() {
        projectName.setError(getString(R.string.error_message_project_name_already_exists));
    }

    private void showUnknownError() {
        projectName.setError(getString(R.string.error_message_unknown));
    }
}
