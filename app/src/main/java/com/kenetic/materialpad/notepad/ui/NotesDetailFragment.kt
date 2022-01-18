package com.kenetic.materialpad.notepad.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentNotesDetailBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.notepad.adapters.NotesDetailScreenAdapter
import com.kenetic.materialpad.notepad.dataclass.Notes
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModelFactory

private const val TAG = "NotesDetailFragment"

class NotesDetailFragment : Fragment() {
    private lateinit var notesAdapter: NotesDetailScreenAdapter
    private lateinit var tempNotes: MutableList<Notes>
    private var fromFab = true
    private var notesId = 0
    private lateinit var binding: FragmentNotesDetailBinding
    private val notesViewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory(
            (activity?.application as AppApplication).notesDatabase.notesDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments.let {
            fromFab = it!!.getBoolean("from_fab")
            notesId = it.getInt("notes_id")
        }
        binding = FragmentNotesDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        notesAdapter = NotesDetailScreenAdapter(notesViewModel) {
            addToList(temp = it)
        }
        //todo - add a lambda to add and edit list items

        binding.notesDetailRecycler.adapter = notesAdapter

        tempNotes = if (fromFab) {
            mutableListOf(Notes(isAListItem = false, listItemIsChecked = false, content = ""))
        } else {
            notesViewModel.getById(notesId).notes.toMutableList()
        }
        notesAdapter.submitList(tempNotes)
    }

    private fun addToList(temp: Int) {
        tempNotes.add(temp, Notes(isAListItem = false, listItemIsChecked = false, content = ""))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_top, menu)
        //super.onCreateOptionsMenu(menu, inflater)
    }

}