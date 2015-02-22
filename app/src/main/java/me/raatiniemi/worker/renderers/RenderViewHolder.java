package me.raatiniemi.worker.renderers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class RenderViewHolder<T> extends RecyclerView.ViewHolder
{
    private Context context;

    public RenderViewHolder(View view)
    {
        super(view);

        this.context = view.getContext();
    }

    protected Context getContext()
    {
        return this.context;
    }

    public abstract void onBindViewHolder(T item);
}
