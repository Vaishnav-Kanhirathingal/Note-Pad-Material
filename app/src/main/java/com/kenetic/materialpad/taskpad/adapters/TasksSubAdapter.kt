package com.kenetic.materialpad.taskpad.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.TaskOrNotesItemBinding
import com.kenetic.materialpad.taskpad.dataclass.Task

class TasksSubAdapter() : ListAdapter<Task, TasksSubAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: TaskOrNotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tasks: Task) {
            binding.editText.setText(tasks.task)
            binding.checkbox.isChecked = tasks.isDone
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem.task == newItem.task

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem.task == newItem.task
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(TaskOrNotesItemBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}