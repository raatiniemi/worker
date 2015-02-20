package me.raatiniemi.worker.renderers;

import android.view.ViewGroup;

public abstract class Renderer
{
    public abstract RenderViewHolder onCreateViewHolder(ViewGroup viewGroup);
}
