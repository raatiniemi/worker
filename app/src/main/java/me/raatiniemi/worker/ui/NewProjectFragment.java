package me.raatiniemi.worker.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.model.domain.project.Project;
import me.raatiniemi.worker.util.Keyboard;

public class NewProjectFragment extends DialogFragment implements DialogInterface.OnShowListener {
    private static final String TAG = "NewProjectFragment";

    /**
     * Callback handler for the "OnCreateProjectListener".
     */
    private OnCreateProjectListener mOnCreateProjectListener;

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
        // Force the keyboard to show when the dialog is showing.
        Keyboard.show(getActivity());
    }

    /**
     * Create a new project.
     *
     * @param view Project name text field.
     */
    private void createNewProject(EditText view) {
        // Retrieve the project name from the text field.
        String name = view.getText().toString();
        if (TextUtils.isEmpty(name)) {
            new AlertDialog.Builder(getActivity())
                .setTitle(R.string.fragment_new_project_create_without_name_title)
                .setMessage(R.string.fragment_new_project_create_without_name_description)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing...
                    }
                })
                .show();

            Log.i(TAG, "No project name have been supplied");
            return;
        }

        Log.d(TAG, "Attempt to create new project with name: " + name);

        // Create the project, and insert it to the database.
        Project project = new Project(name);

        // Replay that the project have been created to the activity.
        if (null != getOnCreateProjectListener()) {
            getOnCreateProjectListener().onCreateProject(project);
        } else {
            Log.w(TAG, "No OnCreateProjectListener have been supplied");
        }
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
         */
        void onCreateProject(Project project);
    }
}
