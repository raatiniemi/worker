package me.raatiniemi.worker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatButton;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.database.TimeDataSource;

public class ProjectsAdapter extends ArrayAdapter<Project>
{
    private static class ViewHolder
    {
        TextView name;
        TextView time;
        TextView description;
        FlatButton clockActivity;
    }

    public ProjectsAdapter(Context context, ArrayList<Project> projects)
    {
        super(context, R.layout.projects_item, projects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the project item for the row.
        final Project project = getItem(position);

        // Check if an existing view is being reused,
        // otherwise inflate the view.
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.projects_item, parent, false);

            // Initialize the view holder and retrieve the view components.
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.project_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.project_time);
            viewHolder.description = (TextView) convertView.findViewById(R.id.project_description);
            viewHolder.clockActivity = (FlatButton) convertView.findViewById(R.id.project_clock_activity);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view.
        viewHolder.name.setText(project.getName());
        viewHolder.time.setText(project.summarizeTime());
        viewHolder.description.setText(project.getDescription());

        // If the description is empty the the
        // description field should be hidden.
        int visibility = View.VISIBLE;
        if (viewHolder.description.getText().length() == 0) {
            visibility = View.GONE;
        }
        viewHolder.description.setVisibility(visibility);

        int activity = R.string.projects_item_project_clock_in;
        if (project.isActive()) {
            activity = R.string.projects_item_project_clock_out;
        }

        viewHolder.clockActivity.setText(getContext().getResources().getString(activity));
        viewHolder.clockActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeDataSource time = new TimeDataSource(getContext());
                time.startTimerForProject(project.getId());

                // TODO: Add time object to project.
                // TODO: Notify the adapter of a data change.
                // TODO: Handle clock out.
            }
        });

        return convertView;
    }
}
