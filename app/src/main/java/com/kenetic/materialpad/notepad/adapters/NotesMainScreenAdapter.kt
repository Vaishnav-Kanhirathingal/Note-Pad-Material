package com.kenetic.materialpad.notepad.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.NotesListItemBinding
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel

class NotesMainScreenAdapter(var viewModel: NotesViewModel) :
    ListAdapter<Int, NotesMainScreenAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: NotesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notesData: NotesData) {
            binding.notesCardTitle.text = notesData.title
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
            NotesListItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //a live data observer that send the class data to the viewHolder
    }
}