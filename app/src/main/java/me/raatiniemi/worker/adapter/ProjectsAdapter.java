package me.raatiniemi.worker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.Project;

public class ProjectsAdapter extends ArrayAdapter<Project>
{
    private static class ViewHolder
    {
        TextView name;
        TextView time;
        TextView description;
    }

    public ProjectsAdapter(Context context, ArrayList<Project> projects)
    {
        super(context, R.layout.projects_item, projects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the project item for the row.
        Project project = getItem(position);

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

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view.
        viewHolder.name.setText(project.getName());
        viewHolder.description.setText(project.getDescription());

        // If the description is empty the the
        // description field should be hidden.
        int visibility = View.VISIBLE;
        if (viewHolder.description.getText().length() == 0) {
            visibility = View.GONE;
        }
        viewHolder.description.setVisibility(visibility);

        // TODO: Set the actual values.
        viewHolder.time.setText(null);

        return convertView;
    }
}
