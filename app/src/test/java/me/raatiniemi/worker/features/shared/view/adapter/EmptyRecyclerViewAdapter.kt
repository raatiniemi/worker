package me.raatiniemi.worker.features.shared.view.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EmptyRecyclerViewAdapter : RecyclerView.Adapter<EmptyRecyclerViewAdapter.ViewHolder>() {
    override fun getItemCount() = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}
