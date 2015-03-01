package me.raatiniemi.worker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.application.Worker;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.exception.NamelessProjectException;
import me.raatiniemi.worker.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;

public class NewProjectFragment extends DialogFragment
{
    /**
     * Public interface for the "OnCreateProjectListener".
     */
    public interface OnCreateProjectListener
    {
        /**
         * When a new project have been created the project is sent to this method.
         * @param project The newly created project.
         */
        public void onCreateProject(Project project);
    }

    /**
     * Callback handler for the "OnCreateProjectListener".
     */
    private OnCreateProjectListener mOnCreateProjectListener;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try {
            mOnCreateProjectListener = (OnCreateProjectListener) activity;
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

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_new_project, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle("Create new project");

        // Add the click listener for the create button.
        FlatButton create = (FlatButton) view.findViewById(R.id.fragment_new_project_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createNewProject(view);
            }
        });

        // Add the click listener for the cancel button.
        FlatButton cancel = (FlatButton) view.findViewById(R.id.fragment_new_project_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }

    /**
     * Create a new project.
     * @param view View for the fragment.
     */
    private void createNewProject(View view)
    {
        try {
            // Retrieve the supplied project name from the text field.
            EditText textField = (EditText) view.findViewById(R.id.fragment_new_project_name);
            String name = textField.getText().toString();

            // Check that the user actually supplied a project name.
            if (name.length() == 0) {
                Log.e("createNewProject", "No project name have been supplied");
                throw new NamelessProjectException();
            }

            Log.d("createNewProject", "Attempt to create new project with name: " + name);

            // Attempt to create the new project with supplied name.
            ProjectMapper projectMapper = MapperRegistry.getProjectMapper();

            // Create the project, and insert it to the database.
            Project project = new Project(name);
            project = projectMapper.insert(project);

            // Replay that the project have been created to the activity.
            mOnCreateProjectListener.onCreateProject(project);

            String message = "Project '" + name + "' have been created";
            Toast.makeText(Worker.getContext(), message, Toast.LENGTH_SHORT).show();

            // We are finished with the project creation,
            // we now have to dismiss the dialog.
            dismiss();
        } catch (NamelessProjectException e) {
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
        } catch (ProjectAlreadyExistsException e) {
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

    public void setOnCreateProjectListener(OnCreateProjectListener onCreateProjectListener)
    {
        mOnCreateProjectListener = onCreateProjectListener;
    }

    public OnCreateProjectListener getOnCreateProjectListener()
    {
        return mOnCreateProjectListener;
    }
}
