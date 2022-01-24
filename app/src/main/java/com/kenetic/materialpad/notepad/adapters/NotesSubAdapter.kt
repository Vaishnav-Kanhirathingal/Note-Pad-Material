package com.kenetic.materialpad.notepad.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.TaskOrNotesItemBinding
import com.kenetic.materialpad.notepad.dataclass.Notes

class NotesSubAdapter() : ListAdapter<Notes, NotesSubAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: TaskOrNotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notes: Notes) {
            binding.checkbox.visibility = if (notes.isAListItem) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.editText.setText(notes.content)
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Notes>() {
            override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean =
                oldItem.content == newItem.content

            override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean =
                oldItem.content == newItem.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskOrNotesItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}