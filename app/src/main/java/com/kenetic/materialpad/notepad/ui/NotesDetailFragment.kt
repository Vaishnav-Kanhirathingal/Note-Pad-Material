package com.kenetic.materialpad.notepad.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentNotesDetailBinding
import com.kenetic.materialpad.databinding.PromptConfirmationBinding
import com.kenetic.materialpad.databinding.PromptTitleBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.notepad.adapters.NotesDetailScreenAdapter
import com.kenetic.materialpad.notepad.dataclass.Note
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "NotesDetailFragment"

class NotesDetailFragment : Fragment() {
    private lateinit var binding: FragmentNotesDetailBinding
    private val notesViewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory(
            (activity?.application as AppApplication).appGeneralDatabase.notesDao()
        )
    }
    private lateinit var notesAdapter: NotesDetailScreenAdapter
    private lateinit var currentNotesData: NotesData
    private var unSaved = true
    private var notesId = 0
    private var fileChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments.let {
            unSaved = it!!.getBoolean("from_fab")
            notesId = it.getInt("notes_id")
        }
        binding = FragmentNotesDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        notesAdapter =
            NotesDetailScreenAdapter(notesViewModel, viewLifecycleOwner)//---------------set-adapter
        setResetTempNotes()
        binding.apply {
            notesDetailRecycler.apply {
                layoutManager = GridLayoutManager(requireContext(), 1)
                adapter = notesAdapter
            }
        }
        notesViewModel.tempNotes.observe(viewLifecycleOwner) {
            // TODO: if size changes, only then submit the list
            notesAdapter.submitList(it)
        }
        setBottomControls()
        setSideControls()
    }

    private fun setBottomControls() {
        binding.apply {
            checklistLayout.setOnClickListener {
                notesViewModel.changeIsListItemAtTempNotes()
            }
            addTabLayout.setOnClickListener {
                notesViewModel.addTabAtTempNotes()
            }
            shareLayout.setOnClickListener {
                notesViewModel.shareNotes(currentNotesData.title, currentNotesData.isFavourite)
            }
            favouriteLayout.setOnClickListener {
                currentNotesData.isFavourite = !currentNotesData.isFavourite
                setFavouriteIcon()
            }
        }
    }

    private fun setSideControls() {
        binding.apply {
            sideControlsSave.setOnClickListener {
                Log.i(TAG, "save menu button working properly")
                saveTempNotes()
            }
            val promptBinding = PromptConfirmationBinding.inflate(layoutInflater)
            val alertDialog = Dialog(requireContext())
            alertDialog.apply {
                setContentView(promptBinding.root)
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setCancelable(true)
            }//---------------------------------------------------------------------------common
            sideControlsRestore.setOnClickListener {
                Log.i(TAG, "restore menu button working properly")
                alertDialog.apply {
                    promptBinding.apply {
                        questionTextView.setText(R.string.restore_note_list_question)
                        confirmationButton.setOnClickListener {
                            Log.i(TAG, "recover menu button working properly")
                            setResetTempNotes()
                            dismiss()
                        }
                        cancelButton.setOnClickListener {
                            dismiss()
                        }
                    }
                    show()
                }
            }
            sideControlsDelete.setOnClickListener {
                alertDialog.apply {
                    promptBinding.apply {
                        questionTextView.setText(R.string.delete_note_List_question)
                        confirmationButton.setOnClickListener {
                            if (!unSaved) {
                                notesViewModel.delete(currentNotesData)
                            }
                            notesViewModel.resetModel()
                            findNavController().navigate(
                                NotesDetailFragmentDirections.actionNotesDetailFragmentToNotesListFragment()
                            )
                        }
                        cancelButton.setOnClickListener {
                            dismiss()
                        }
                    }
                    show()
                }
                // TODO: add prompt
                Log.i(TAG, "delete menu button working properly")
            }
        }
    }

    private fun setFavouriteIcon() {
        binding.favouriteImage.setImageResource(
            if (currentNotesData.isFavourite) {
                R.drawable.star_selected_24
            } else {
                R.drawable.star_unselected_24
            }
        )
    }

    private fun setResetTempNotes() {
        if (unSaved) {
            notesViewModel.setTempNotes(
                mutableListOf(
                    Note(isAListItem = false, listItemIsChecked = false, content = "")
                )
            )
            currentNotesData = NotesData(
                notes = notesViewModel.tempNotes.value!!,
                isFavourite = false,
                title = "Untitled",
                dateFormatted = System.currentTimeMillis()
            )
        } else {
            Log.i(TAG, "integer sent - $notesId [${notesId.javaClass}]")
            notesViewModel.getById(notesId).asLiveData().observe(viewLifecycleOwner) {
                currentNotesData = it
                notesViewModel.setTempNotes(currentNotesData.notes.toMutableList())
            }
        }
        fileChanged = false
        setFavouriteIcon()
    }

    private fun saveTempNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            currentNotesData.apply {
                notes = notesViewModel.tempNotes.value!!
                isFavourite = this@NotesDetailFragment.currentNotesData.isFavourite
                title = this@NotesDetailFragment.currentNotesData.title
                dateFormatted = System.currentTimeMillis()
            }
            if (unSaved) {
                notesViewModel.insert(currentNotesData)
            } else {
                notesViewModel.update(currentNotesData)
            }
            unSaved = false
            fileChanged = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_top, menu)
        //super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.title_change -> {
                Log.i(TAG, "prompt title changer layout")
                val promptBinding = PromptTitleBinding.inflate(layoutInflater)
                val alertDialog = Dialog(requireContext())
                alertDialog.apply {
                    setContentView(promptBinding.root)
                    window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setCancelable(true)
                    promptBinding.apply {
                        titleEditText.setText(currentNotesData.title)
                        cancelButton.setOnClickListener { alertDialog.dismiss() }
                        changeButton.setOnClickListener {
                            currentNotesData.title = titleEditText.text.toString()
                            alertDialog.dismiss()
                        }
                    }
                    show()
                }
                true
            }

            else -> {
                Log.e(TAG, "undetected options item detected")
                super.onOptionsItemSelected(item)
            }
        }
    }
}