package com.kenetic.materialpad.notepad.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.TaskOrNotesItemBinding
import com.kenetic.materialpad.notepad.dataclass.Notes
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel

class NotesDetailScreenAdapter(
    var viewModel: NotesViewModel,
    private val listAdder: (Int) -> Unit,
    private val listRemover: (Int) -> Unit,
    private val fileChanged: (
        position: Int,
        text: String,
        isAListItem: Boolean,
        ListItemIsChecked: Boolean
    ) -> Unit
) :
    ListAdapter<Notes, NotesDetailScreenAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: TaskOrNotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            notes: Notes,
            position: Int,
            listAdder: (Int) -> Unit,
            listRemover: (Int) -> Unit,
            fileChanged: (
                position: Int,
                text: String,
                isAListItem: Boolean,
                ListItemIsChecked: Boolean
            ) -> Unit
        ) {
            binding.checkbox.isChecked = notes.listItemIsChecked
            binding.editText.setText(notes.content)
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Notes>() {
            override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TaskOrNotesItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position, listAdder,listRemover,fileChanged)
    }
}