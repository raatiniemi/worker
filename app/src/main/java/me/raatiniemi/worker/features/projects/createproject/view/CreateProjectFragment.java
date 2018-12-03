/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.projects.createproject.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.features.projects.ViewModels;
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEvent;
import me.raatiniemi.worker.features.projects.createproject.viewmodel.CreateProjectViewModel;
import me.raatiniemi.worker.features.shared.view.fragment.RxDialogFragment;
import me.raatiniemi.worker.util.Keyboard;
import timber.log.Timber;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.RxUtil.applySchedulers;

public class CreateProjectFragment extends RxDialogFragment implements DialogInterface.OnShowListener {
    private final EventBus eventBus = EventBus.getDefault();

    private final ViewModels viewModels = new ViewModels();
    private final CreateProjectViewModel.ViewModel viewModel = viewModels.getCreateProject();

    private EditText projectName;
    private AppCompatButton projectSubmit;

    public static CreateProjectFragment newInstance() {
        return new CreateProjectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        projectName = view.findViewById(R.id.etProjectName);
        projectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.input().projectName(s.toString());
            }
        });
        projectName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.createProject();
                return true;
            }

            return false;
        });

        projectSubmit = view.findViewById(R.id.btnCreate);
        projectSubmit.setOnClickListener(l -> viewModel.input().createProject());

        AppCompatButton dismiss = view.findViewById(R.id.btnDismiss);
        dismiss.setOnClickListener(l -> dismiss());

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
