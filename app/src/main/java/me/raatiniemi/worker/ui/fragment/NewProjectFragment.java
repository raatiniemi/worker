package me.raatiniemi.worker.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cengalabs.flatui.views.FlatButton;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.Project;
import me.raatiniemi.worker.database.ProjectDataSource;

public class NewProjectFragment extends DialogFragment implements View.OnClickListener
{
    private OnCreateProjectListener mCallback;

    public interface OnCreateProjectListener
    {
        public void onCreateProject(Project project);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try {
            mCallback = (OnCreateProjectListener) activity;
        } catch (ClassCastException e) {
            Log.e("onAttach", activity.toString() +" do not implement OnCreateProjectListener");

            // We're unable to use the activity since it do not implement
            // the 'OnCreateProjectListener', display error message to user.
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.fragment_new_project_class_cast_exception_title))
                    .setMessage(getString(R.string.fragment_new_project_class_cast_exception_description))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing...
                        }
                    })
                    .show();

            // Dismiss the dialog since we are unable
            // to properly create projects with it.
            dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().setTitle("Create new project");

        View view = inflater.inflate(R.layout.fragment_new_project, container, false);

        // Add the click listener for the create button.
        FlatButton create = (FlatButton) view.findViewById(R.id.fragment_new_project_create);
        create.setOnClickListener(this);

        // Add the click listener for the cancel button.
        FlatButton cancel = (FlatButton) view.findViewById(R.id.fragment_new_project_cancel);
        cancel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.fragment_new_project_create:
                createNewProject();
                break;
            case R.id.fragment_new_project_cancel:
                dismiss();
                break;
        }
    }

    private void createNewProject()
    {
        try {
            // Retrieve the supplied project name from the text field.
            EditText textField = (EditText) getView().findViewById(R.id.fragment_new_project_name);
            String projectName = textField.getText().toString();

            // Check that the user actually supplied a project name.
            if (projectName.length() == 0) {
                // No project name supplied, display error message to user.
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.fragment_new_project_create_without_name_title)
                        .setMessage(R.string.fragment_new_project_create_without_name_description)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing...
                            }
                        })
                        .show();

                // Exit method, no need to go further.
                return;
            }

            ProjectDataSource dataSource = new ProjectDataSource(getActivity());

            // Check if a project with the supplied name already exists.
            Project project = dataSource.findProjectByName(projectName);
            if (project != null) {
                // Project name already exists, display error message to user.
                new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.fragment_new_project_create_project_already_exists_title))
                    .setMessage(getString(R.string.fragment_new_project_create_project_already_exists_description))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing...
                        }
                    })
                    .show();

                // Exit method, no need to go further.
                return;
            }

            // Attempt to create the new project with supplied name.
            Log.d("createNewProject", "Attempt to create new project with name: "+ projectName);
            dataSource.createNewProject(projectName);

            // TODO: Relay data update to the project activity.

            // We are finished with the project creation,
            // we now have to dismiss the dialog.
            dismiss();
        } catch (NullPointerException e) {
            // Was unable to find the EditText component, display error message to the user.
            new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.fragment_new_project_null_pointer_exception_title))
                .setMessage(getString(R.string.fragment_new_project_null_pointer_exception_description))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing...
                    }
                })
                .show();
        }
    }
}
