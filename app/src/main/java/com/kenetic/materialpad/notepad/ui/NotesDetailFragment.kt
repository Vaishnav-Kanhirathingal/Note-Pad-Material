package com.kenetic.materialpad.notepad.ui

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
    private var isFavourite = false
    private var title = "Untitled"
    private var fileChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        notesAdapter = NotesDetailScreenAdapter(notesViewModel)//------------------------set-adapter
        setResetTempNotes()
        binding.apply {
            notesDetailRecycler.apply {
                layoutManager = GridLayoutManager(requireContext(), 1)
                adapter = notesAdapter
            }
            //-----------------------------------------------------------------------bottom-controls
            checklistLayout.setOnClickListener {
                notesViewModel.changeIsListItemAtTempNotes()
            }
            addTabLayout.setOnClickListener {
                notesViewModel.addTabAtTempNotes()
            }
            shareLayout.setOnClickListener {
                notesViewModel.shareNotes()
            }
            favouriteLayout.setOnClickListener {
                isFavourite = !isFavourite
                setFavouriteIcon()
            }
        }
        notesViewModel.tempNotes.observe(viewLifecycleOwner) {
            // TODO: if size changes, only then submit the list
            notesAdapter.submitList(it)
        }
    }

    private fun setFavouriteIcon() {
        binding.favouriteImage.setImageResource(
            if (isFavourite) {
                R.drawable.star_selected_24
            } else {
                R.drawable.star_unselected_24
            }
        )
    }

    private fun setResetTempNotes() {
        if (unSaved) {
            CoroutineScope(Dispatchers.IO).launch {
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
            }
        } else {
            Log.i(TAG, "integer sent - $notesId [${notesId.javaClass}]")
            notesViewModel.getById(notesId).asLiveData().observe(viewLifecycleOwner) {
                it.apply {
                    Log.i(TAG, "id detected = $id")
                    Log.i(TAG, "date detected = $dateFormatted")
                    Log.i(TAG, "isFavorite detected = $isFavourite")
                    Log.i(TAG, "title detected = $title")
                    Log.i(TAG, "notes size detected = ${notes.size}")
                }
                currentNotesData = it
                notesViewModel.setTempNotes(currentNotesData.notes.toMutableList())
            }
        }
        fileChanged = false
        setFavouriteIcon()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTempNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            currentNotesData.apply {
                notes = notesViewModel.tempNotes.value!!
                isFavourite = this@NotesDetailFragment.isFavourite
                title = this@NotesDetailFragment.title
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.details_delete -> {
                Log.i(TAG, "delete menu button working properly")
                //todo - add a function to delete active list item
                if (!unSaved) {
                    notesViewModel.delete(currentNotesData)
                }
                // TODO: add a viewModel values resetting function
                findNavController().navigate(
                    NotesDetailFragmentDirections.actionNotesDetailFragmentToNotesListFragment()
                )
                true
            }

            R.id.details_recover -> {
                Log.i(TAG, "recover menu button working properly")
                setResetTempNotes()
                true
            }

            R.id.details_save -> {
                Log.i(TAG, "save menu button working properly")
                saveTempNotes()
                true
            }

            else -> {
                Log.e(TAG, "undetected options item detected")
                super.onOptionsItemSelected(item)
            }
        }
    }
}