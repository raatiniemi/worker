package me.raatiniemi.worker.base.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

abstract public class ListAdapter<T extends List> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;

    protected Context getContext()
    {
        return mContext;
    }

    public ListAdapter(Context context)
    {
        mContext = context;
    }
}
