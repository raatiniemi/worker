package me.raatiniemi.worker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Time;

public class ProjectTimeListAdapter extends RecyclerView.Adapter<ProjectTimeListAdapter.TimeViewHolder>
{
    private ArrayList<Time> mTime;

    public ProjectTimeListAdapter(ArrayList<Time> time)
    {
        mTime = time;
    }

    @Override
    public int getItemCount()
    {
        return mTime.size();
    }

    @Override
    public void onBindViewHolder(TimeViewHolder timeViewHolder, int index)
    {
        // TODO: Implement onBindViewHolder for the ProjectTimeListAdapter.
    }

    @Override
    public TimeViewHolder onCreateViewHolder(ViewGroup viewGroup, int index)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View timeView = inflater.inflate(R.layout.project_time_list_item, viewGroup, false);

        return new TimeViewHolder(timeView);
    }

    public static class TimeViewHolder extends RecyclerView.ViewHolder
    {
        public TimeViewHolder(View view)
        {
            super(view);
        }
    }
}
