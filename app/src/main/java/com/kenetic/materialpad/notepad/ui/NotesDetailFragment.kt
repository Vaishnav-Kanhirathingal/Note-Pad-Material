package com.kenetic.materialpad.notepad.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
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
import java.text.SimpleDateFormat

private const val TAG = "NotesDetailFragment"

class NotesDetailFragment : Fragment() {
    private lateinit var notesAdapter: NotesDetailScreenAdapter
    private val _tempNotes: MutableLiveData<MutableList<Note>> = MutableLiveData(mutableListOf())
    private val tempNotes: MutableLiveData<MutableList<Note>> get() = _tempNotes
    private lateinit var currentNotesData: NotesData
    private var fromFab = true
    private var notesId = 0
    private lateinit var binding: FragmentNotesDetailBinding
    private val notesViewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory(
            (activity?.application as AppApplication).appGeneralDatabase.notesDao()
        )
    }
    private var isFavourite = false
    private var title = "Untitled"
    private var fileChanged = false
    private var activeElement = 0

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        notesAdapter = NotesDetailScreenAdapter(
            viewModel = notesViewModel,
            listAdder = {
                _tempNotes.value!!
                    .add(it, Note(isAListItem = false, listItemIsChecked = false, content = ""))
            },
            listRemover = { _tempNotes.value!!.removeAt(it) },
            fileChanged = { position: Int,
                            text: String,
                            isList: Boolean,
                            isChecked: Boolean ->
                fileChanged = true
                _tempNotes.value!![position].apply {
                    content = text
                    isAListItem = isList
                    listItemIsChecked = isChecked
                }
            }
        )
        setResetTempNotes()
        binding.apply {
            notesDetailRecycler.apply {
                layoutManager = GridLayoutManager(requireContext(), 1)
                adapter = notesAdapter
            }
            //-----------------------------------------------------------------------bottom-controls
            checklistLayout.setOnClickListener {
                _tempNotes.value!![activeElement].isAListItem = true
            }
            addTabLayout.setOnClickListener {
                _tempNotes.value!![activeElement].content.plus("\t")
            }
            shareLayout.setOnClickListener {
                share()
            }
            favouriteLayout.setOnClickListener {
                isFavourite = !isFavourite
                setFavouriteIcon()
            }
        }
        tempNotes.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it)
        }
    }

    private fun setResetTempNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            if (fromFab) {
                _tempNotes.postValue(
                    mutableListOf(
                        Note(
                            isAListItem = false,
                            listItemIsChecked = false,
                            content = ""
                        )
                    )
                )
                currentNotesData = NotesData(
                    notes = tempNotes.value!!,
                    isFavourite = false,
                    title = this@NotesDetailFragment.title,
                    dateFormatted = System.currentTimeMillis()
                )
            } else {
                currentNotesData = notesViewModel.getById(notesId).asLiveData().value!!
                _tempNotes.postValue(currentNotesData.notes.toMutableList())
            }
            fileChanged = false
            setFavouriteIcon()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTempNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            currentNotesData.apply {
                notes = tempNotes.value!!
                isFavourite = this@NotesDetailFragment.isFavourite
                title = this@NotesDetailFragment.title
                dateFormatted = System.currentTimeMillis()
            }
            if (fromFab) {
                notesViewModel.insert(currentNotesData)
            } else {
                notesViewModel.update(currentNotesData)
            }
            fromFab = false
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
                if (!fromFab) {
                    notesViewModel.delete(currentNotesData)
                }
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

    private fun setFavouriteIcon() {
        binding.favouriteImage.setImageResource(
            if (currentNotesData.isFavourite) {
                R.drawable.star_selected_24
            } else {
                R.drawable.star_unselected_24
            }
        )
    }

    private fun share() {
        CoroutineScope(Dispatchers.IO).launch {
            var sharableText = "date last edited - ${
                SimpleDateFormat("HH:mm dd:MM:yy").format(System.currentTimeMillis())
            }"
            tempNotes.value!!.size.let {
                for (i in 0 until it) {
                    val tempOne = tempNotes.value!![i]
                    sharableText += "\n${
                        if (tempOne.isAListItem) {
                            if (tempOne.listItemIsChecked) {
                                "[#] - "
                            } else {
                                "[ ] - "
                            }
                        } else {
                            ""
                        }
                    }${tempOne.content}"
                }
            }
            Log.i(TAG, "\n$sharableText")
            // TODO: start intent for sharing
        }
    }
}