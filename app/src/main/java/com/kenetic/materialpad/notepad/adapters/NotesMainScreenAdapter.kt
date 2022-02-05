package com.kenetic.materialpad.notepad.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.NotesListItemBinding
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.notepad.ui.NotesListFragment
import com.kenetic.materialpad.notepad.ui.NotesListFragmentDirections
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "NotesMainScreenAdapter"

class NotesMainScreenAdapter(
    private var viewModel: NotesViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private var fragInstance: NotesListFragment,
    private val context: Context
) :
    ListAdapter<Int, NotesMainScreenAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: NotesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(notesData: NotesData, context: Context) {
            binding.notesCardTitle.text = notesData.title
            binding.notesLastUpdated.text =
                SimpleDateFormat("dd/MM/yy").format(Date(notesData.dateFormatted))
            val adapter = NotesSubAdapter()
            binding.NotesContentRecycler.layoutManager = GridLayoutManager(context, 1)
            binding.NotesContentRecycler.adapter = adapter
            val tempListNotes = notesData.notes.toMutableList()
            Log.i(TAG, "size of list item - ${tempListNotes.size}")
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
            if (it.notes.size > 3) {
                temp.notes = it.notes.subList(0, 3)
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
            holder.bind(temp, context)
//            val temp = viewModel.getById(getItem(position)).asLiveData().value!!
//            Log.i(TAG, "notes id - ${temp.id}")
//            if (temp.notes.size > 3) {
//                temp.notes = temp.notes.subList(0, 3)
//            }
//            holder.itemView.setOnClickListener {
//                findNavController(fragInstance).navigate(
//                    NotesListFragmentDirections.actionNotesListFragmentToNotesDetailFragment(
//                        fromFab = false,
//                        notesId = temp.id
//                    )
//                )
//            }
//            holder.bind(temp)
        }
    }
}