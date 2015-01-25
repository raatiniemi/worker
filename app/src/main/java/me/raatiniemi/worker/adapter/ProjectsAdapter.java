package me.raatiniemi.worker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatButton;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>
{
    private ArrayList<Project> mProjects;

    public ProjectsAdapter(ArrayList<Project> projects)
    {
        mProjects = projects;
    }

    @Override
    public int getItemCount()
    {
        return mProjects.size();
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder projectViewHolder, int index)
    {
        Project project = mProjects.get(index);

        projectViewHolder.mName.setText(project.getName());
        projectViewHolder.mTime.setText(project.summarizeTime());
        projectViewHolder.mDescription.setText(project.getDescription());

        // If the project description is empty
        // the view should be hidden.
        int visibility = View.VISIBLE;
        if (projectViewHolder.mDescription.getText().length() == 0) {
            visibility = View.INVISIBLE;
        }
        projectViewHolder.mDescription.setVisibility(visibility);
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int index)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View projectView = inflater.inflate(R.layout.projects_item, viewGroup, false);

        return new ProjectViewHolder(projectView);
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView mName;
        protected TextView mTime;
        protected TextView mDescription;
        protected FlatButton mClockActivity;

        public ProjectViewHolder(View view)
        {
            super(view);

            mName = (TextView) view.findViewById(R.id.project_name);
            mTime = (TextView) view.findViewById(R.id.project_time);
            mDescription = (TextView) view.findViewById(R.id.project_description);
            mClockActivity = (FlatButton) view.findViewById(R.id.project_clock_activity);
        }
    }
}
