package com.kenetic.materialpad.taskpad.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.TaskOrNotesItemBinding
import com.kenetic.materialpad.taskpad.dataclass.Task
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModel

private const val TAG = "TasksDetailScreenAdapter"

class TasksDetailScreenAdapter() :
    ListAdapter<Task, TasksDetailScreenAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: TaskOrNotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tasks: Task) {
            binding.editText.setText(tasks.task)
            binding.checkbox.isChecked = tasks.isDone
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
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
        holder.bind(getItem(position))
        Log.i(TAG, "recycler binding performed")
    }
}