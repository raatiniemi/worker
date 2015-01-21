package me.raatiniemi.worker.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cengalabs.flatui.views.FlatButton;

import me.raatiniemi.worker.R;

public class NewProjectFragment extends DialogFragment implements View.OnClickListener
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                createNewProject(view);
                break;
            case R.id.fragment_new_project_cancel:
                dismiss();
                break;
        }
    }

    private void createNewProject(View view)
    {
        // TODO: Implement creation of new projects.
    }
}
