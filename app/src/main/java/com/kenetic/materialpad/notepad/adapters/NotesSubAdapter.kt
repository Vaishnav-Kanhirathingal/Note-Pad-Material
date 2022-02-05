package com.kenetic.materialpad.notepad.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.TaskOrNotesItemBinding
import com.kenetic.materialpad.notepad.dataclass.Note

private const val TAG = "NotesSubAdapter"

class NotesSubAdapter() : ListAdapter<Note, NotesSubAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: TaskOrNotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            Log.i(TAG, "bind called")
            binding.apply {
                checkbox.visibility = if (note.isAListItem) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                checkbox.isChecked = note.listItemIsChecked
                editText.setText(note.content)

                checkbox.isEnabled = false
                editText.isEnabled = false
            }
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
                oldItem.content == newItem.content

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
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