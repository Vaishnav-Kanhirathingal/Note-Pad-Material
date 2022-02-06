package com.kenetic.materialpad.notepad.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.TaskOrNotesItemBinding
import com.kenetic.materialpad.notepad.dataclass.Note
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel

private const val TAG = "NotesDetailScreenAdapter"

class NotesDetailScreenAdapter(
    private var viewModel: NotesViewModel,
    private var lifecycleOwner: LifecycleOwner
) :
    ListAdapter<Note, NotesDetailScreenAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(val binding: TaskOrNotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            note: Note,
            position: Int,
            viewModel: NotesViewModel,
            lifecycleOwner: LifecycleOwner
        ) {
            val previousText = ""
            binding.apply {
                    Log.i(TAG,"view updated")
                    checkbox.visibility = if (note.isAListItem) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    checkbox.isChecked = note.listItemIsChecked
                editText.setText(note.content)

                checkbox.setOnClickListener {//-----------------------------------check-box-listener
                    viewModel.changeCheckedStatus(position, checkbox.isChecked)
                }

                //---------------------------------------------------------------------text-listener
                editText.addTextChangedListener {
                    val currText = it.toString()
                    when {
                        previousText.length == currText.length -> {//-------------backspace-detected
                            viewModel.onBackSpaceKey(position, currText)
                        }
                        "\n" in currText -> {//---------------------------------------enter-detected
                            val temp = currText.split("\n", limit = 2)
                            editText.setText(temp[0])
                            viewModel.onEnterKey(position, temp[1])
                        }
                        else -> {//----------------------------------------------------normal-update
                            viewModel.textUpdater(
                                position, currText
                            )
                            viewModel.setActive(position)
                        }
                    }
                }
                editText.setOnFocusChangeListener { _, hasFocus: Boolean ->
                    if (hasFocus) {
                        Log.i(TAG, "focus on position - $position")
                        viewModel.activeElement = position
                    } else if (position == viewModel.activeElement) {
                        editText.requestFocus()
                        Log.i(TAG, "focus on position - $position")
                    }
                }
            }
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.isAListItem == newItem.isAListItem
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.content == newItem.content
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskOrNotesItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position, viewModel, lifecycleOwner)
    }
}