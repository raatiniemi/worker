package me.raatiniemi.worker.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.raatiniemi.worker.R;

public class NewProjectFragment extends DialogFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Create new project");
        return inflater.inflate(R.layout.fragment_new_project, container, false);
    }
}
