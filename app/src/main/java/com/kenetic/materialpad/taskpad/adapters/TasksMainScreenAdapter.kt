package com.kenetic.materialpad.taskpad.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.TasksListItemBinding
import com.kenetic.materialpad.taskpad.dataclass.TasksData
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModel

class TasksMainScreenAdapter(var viewModel: TasksViewModel) :
    ListAdapter<Int, TasksMainScreenAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: TasksListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(taskData: TasksData) {
            binding.tasksCardTitle.text = taskData.title
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem==newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TasksListItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //a live data observer that send the class data to the viewHolder
    }
}