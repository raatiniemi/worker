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
import me.raatiniemi.worker.util.DateIntervalFormatter;

public class ProjectTimeListAdapter extends RecyclerView.Adapter<ProjectTimeListAdapter.ItemViewHolder>
{
    private ArrayList<Time> mTime;

    private EventListener mEventListener;

    private View.OnLongClickListener mItemViewOnLongClickListener;

    public interface EventListener
    {
        public boolean onItemViewLongClick(View view);
    }

    public ProjectTimeListAdapter(ArrayList<Time> time)
    {
        mTime = time;

        mItemViewOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                return onItemViewLongClick(view);
            }
        };
    }

    private boolean onItemViewLongClick(View view)
    {
        return null != getEventListener() && getEventListener().onItemViewLongClick(view);
    }

    @Override
    public int getItemCount()
    {
        return mTime.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int index)
    {
        Time time = mTime.get(index);

        // Register the item view on click listener.
        holder.itemView.setOnLongClickListener(mItemViewOnLongClickListener);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date(time.getStart());
        holder.getDate().setText(dateFormatter.format(date));

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        Date start = new Date(time.getStart());
        holder.getStart().setText(format.format(start));

        if (time.isActive()) {
            holder.getStop().setText(null);
            holder.getSummarize().setText(null);
        } else {
            Date stop = new Date(time.getStop());

            DateIntervalFormatter formatter = new DateIntervalFormatter();
            String summarize = formatter.format(time.getTime());

            holder.getStop().setText(format.format(stop));
            holder.getSummarize().setText(summarize);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int index)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.project_time_list_item, viewGroup, false);

        return new ItemViewHolder(view);
    }

    public Time get(int position)
    {
        return mTime.get(position);
    }

    public void remove(int position)
    {
        mTime.remove(position);
        notifyItemRemoved(position);
    }

    protected static class ItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnLongClickListener
    {
        private TextView mDate;

        private TextView mStart;

        private TextView mStop;

        private TextView mSummarize;

        public ItemViewHolder(View view)
        {
            super(view);

            setDate((TextView) view.findViewById(R.id.project_time_list_item_date));
            setStart((TextView) view.findViewById(R.id.project_time_list_item_start));
            setStop((TextView) view.findViewById(R.id.project_time_list_item_stop));
            setSummarize((TextView) view.findViewById(R.id.project_time_list_item_summarize));

            // Set the long click listener for the view holder.
            view.setOnLongClickListener(this);
        }

        private void setDate(TextView date)
        {
            mDate = date;
        }

        public TextView getDate()
        {
            return mDate;
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

        @Override public boolean onLongClick(View view)
        {
            // TODO: Handle the on long click.
            return true;
        }
    }

    public void setEventListener(EventListener eventListener)
    {
        mEventListener = eventListener;
    }

    public EventListener getEventListener()
    {
        return mEventListener;
    }
}
