package com.kenetic.materialpad.notepad.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.NotesListItemBinding
import com.kenetic.materialpad.notepad.dataclass.Notes
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.notepad.ui.NotesListFragment
import com.kenetic.materialpad.notepad.ui.NotesListFragmentDirections
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*

class NotesMainScreenAdapter(
    private var viewModel: NotesViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private var fragInstance:NotesListFragment
) :
    ListAdapter<Int, NotesMainScreenAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: NotesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(notesData: NotesData) {
            binding.notesCardTitle.text = notesData.title
            binding.notesLastUpdated.text =
                SimpleDateFormat("dd/MM/yy").format(Date(notesData.dateFormatted))
            val adapter = NotesSubAdapter()
            binding.NotesContentRecycler.adapter = adapter

            val tempListNotes = mutableListOf<Notes>()          //notesData.notes

            notesData.listListItemIsChecked.size.let {
                for (i in 0..it) {
                    tempListNotes.add(
                        Notes(
                            isAListItem = notesData.listIsAListItem[i],
                            listItemIsChecked = notesData.listListItemIsChecked[i],
                            content = notesData.listContent[i]
                        )
                    )
                }
            }
            adapter.submitList(tempListNotes)
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NotesListItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewModel.getById(getItem(position)).asLiveData().observe(lifecycleOwner) {
            val temp = it
            if (it.listListItemIsChecked.size > 2) {
                temp.listIsAListItem = it.listIsAListItem.subList(0, 2)
                temp.listContent = it.listContent.subList(0, 2)
                temp.listListItemIsChecked = it.listListItemIsChecked.subList(0, 2)
                //---------------------------------
                temp.notes = it.notes.subList(0, 2)
            }
            holder.itemView.setOnClickListener {
                findNavController(fragInstance).navigate(
                    NotesListFragmentDirections
                        .actionNotesListFragmentToNotesDetailFragment(
                            fromFab = false,
                            notesId = temp.id
                        )
                )
            }
            holder.bind(temp)
        }
    }
}