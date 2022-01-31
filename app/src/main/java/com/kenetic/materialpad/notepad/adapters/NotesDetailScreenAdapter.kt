package com.kenetic.materialpad.notepad.adapters

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.materialpad.databinding.TaskOrNotesItemBinding
import com.kenetic.materialpad.notepad.dataclass.Note
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel

private const val TAG = "NotesDetailScreenAdapter"

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
    ListAdapter<Note, NotesDetailScreenAdapter.ViewHolder>(diffCallBack) {

    class ViewHolder(private val binding: TaskOrNotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            note: Note,
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
            Log.i(TAG, "bind working")
            binding.apply {
                if (note.isAListItem) {
                    checkbox.visibility = View.VISIBLE
                    checkbox.isChecked = note.listItemIsChecked
                } else {
                    checkbox.visibility = View.GONE
                }
                editText.setText(note.content)

                checkbox.setOnClickListener {
                    fileChanged(
                        position,
                        editText.text.toString(),
                        note.isAListItem,
                        note.listItemIsChecked
                    )
                }

                editText.addTextChangedListener {
                    Log.i(TAG, "addTextChangedListener working")
                }

                editText.setOnKeyListener { view, i, keyEvent ->    //not working
                    Log.i(TAG, "onKeyListener working")
                    when (i) {
                        KeyEvent.KEYCODE_ENTER -> {
                            fileChanged(
                                position,
                                editText.text.toString(),
                                note.isAListItem,
                                note.listItemIsChecked
                            )
                            listAdder(position)
                            true
                        }
                        KeyEvent.KEYCODE_DEL -> {
                            fileChanged(
                                position,
                                editText.text.toString(),
                                note.isAListItem,
                                note.listItemIsChecked
                            )
                            if (editText.text.isNullOrEmpty() && position != 0) {
                                listRemover(position)
                            }
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskOrNotesItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position, listAdder, listRemover, fileChanged)
    }
}