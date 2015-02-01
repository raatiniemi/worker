package me.raatiniemi.worker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        Time time = mTime.get(index);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        Date start = new Date(time.getStart());
        timeViewHolder.getStart().setText(format.format(start));

        if (time.isActive()) {
            timeViewHolder.getStop().setText(null);
            timeViewHolder.getSummarize().setText(null);
        } else {
            Date stop = new Date(time.getStop());

            timeViewHolder.getStop().setText(format.format(stop));
            timeViewHolder.getSummarize().setText(time.summarizeTime());
        }
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
        private TextView mStart;

        private TextView mStop;

        private TextView mSummarize;

        public TimeViewHolder(View view)
        {
            super(view);

            setStart((TextView) view.findViewById(R.id.project_time_list_item_start));
            setStop((TextView) view.findViewById(R.id.project_time_list_item_stop));
            setSummarize((TextView) view.findViewById(R.id.project_time_list_item_summarize));
        }

        private void setStart(TextView start)
        {
            mStart = start;
        }

        public TextView getStart()
        {
            return mStart;
        }

        private void setStop(TextView stop)
        {
            mStop = stop;
        }

        public TextView getStop()
        {
            return mStop;
        }

        private void setSummarize(TextView summarize)
        {
            mSummarize = summarize;
        }

        public TextView getSummarize()
        {
            return mSummarize;
        }
    }
}
