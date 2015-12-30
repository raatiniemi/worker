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

package me.raatiniemi.worker.presentation.view.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.util.Keyboard;
import rx.Observable;
import rx.Subscriber;

public class NewProjectFragment extends DialogFragment implements DialogInterface.OnShowListener {
    private static final String TAG = "NewProjectFragment";

    /**
     * Callback handler for the "OnCreateProjectListener".
     */
    private OnCreateProjectListener mOnCreateProjectListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Check that we actually have a listener available, otherwise we
        // should not attempt to create new projects.
        if (null == getOnCreateProjectListener()) {
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_project, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve and set the title for the dialog.
        getDialog().setTitle(getString(R.string.fragment_new_project_title));

        // Retrieve the text field for project name.
        final EditText projectName = (EditText) view.findViewById(R.id.fragment_new_project_name);

        // Add the click listener for the create button.
        TextView create = (TextView) view.findViewById(R.id.fragment_new_project_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the text field to create the new project.
                createNewProject(projectName);
            }
        });

        // Add the click listener for the cancel button.
        TextView cancel = (TextView) view.findViewById(R.id.fragment_new_project_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

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

    /**
     * Create a new project.
     *
     * @param view Project name text field.
     */
    private void createNewProject(final EditText view) {
        // Retrieve the project name from the text field.
        String name = view.getText().toString();
        if (TextUtils.isEmpty(name)) {
            view.setError(getString(R.string.error_message_project_name_missing));

            Log.i(TAG, "No project name have been supplied");
            return;
        }

        Log.d(TAG, "Attempt to create new project with name: " + name);

        // Create the project, and insert it to the database.
        Project project = new Project(name);
        getOnCreateProjectListener().onCreateProject(project)
                .subscribe(new Subscriber<Project>() {
                    @Override
                    public void onNext(Project project) {
                        Log.d(TAG, "createNewProject onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "createNewProject onError");

                        if (e instanceof ProjectAlreadyExistsException) {
                            Log.d(TAG, "Unable to create project, duplicate name");
                            view.setError(getString(R.string.error_message_project_name_already_exists));
                            return;
                        }

                        Log.w(TAG, "Unable to create project: " + e.getMessage());
                        view.setError(getString(R.string.error_message_unknown));
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "createNewProject onCompleted");

                        // The project have been created, we can dismiss the fragment.
                        dismiss();
                    }
                });
    }

    public OnCreateProjectListener getOnCreateProjectListener() {
        return mOnCreateProjectListener;
    }

    public void setOnCreateProjectListener(OnCreateProjectListener onCreateProjectListener) {
        mOnCreateProjectListener = onCreateProjectListener;
    }

    /**
     * Public interface for the "OnCreateProjectListener".
     */
    public interface OnCreateProjectListener {
        /**
         * When a new project have been created the project is sent to this method.
         *
         * @param project The newly created project.
         * @return Observable emitting the created project.
         */
        Observable<Project> onCreateProject(Project project);
    }
}
