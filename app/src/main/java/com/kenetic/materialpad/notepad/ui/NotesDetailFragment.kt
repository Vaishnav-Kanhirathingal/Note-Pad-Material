package com.kenetic.materialpad.notepad.ui

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentNotesDetailBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.notepad.adapters.NotesDetailScreenAdapter
import com.kenetic.materialpad.notepad.dataclass.Notes
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "NotesDetailFragment"

class NotesDetailFragment : Fragment() {
    private lateinit var notesAdapter: NotesDetailScreenAdapter
    private val _tempNotes: MutableLiveData<MutableList<Notes>> = MutableLiveData(mutableListOf())
    private val tempNotes: MutableLiveData<MutableList<Notes>> get() = _tempNotes
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
                    .add(it, Notes(isAListItem = false, listItemIsChecked = false, content = ""))
            },
            listRemover = { _tempNotes.value!!.removeAt(it) },
            fileChanged = { fileChanged = true }
        )
        binding.notesDetailRecycler.layoutManager = GridLayoutManager(this.requireContext(), 1)
        //todo - add a lambda to add and edit list items
        setResetTempNotes()
        binding.notesDetailRecycler.adapter = notesAdapter
        tempNotes.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it)
        }
    }

    private fun setResetTempNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            if (fromFab) {
                _tempNotes.postValue(
                    mutableListOf(
                        Notes(
                            isAListItem = false,
                            listItemIsChecked = false,
                            content = ""
                        )
                    )
                )
            } else {
                currentNotesData = notesViewModel.getById(notesId).asLiveData().value!!
                val temp = mutableListOf<Notes>()
                currentNotesData.listIsAListItem.size.let {
                    for (i in 0..it) {
                        temp.add(
                            Notes(
                                isAListItem = currentNotesData.listIsAListItem[it],
                                listItemIsChecked = currentNotesData.listListItemIsChecked[it],
                                content = currentNotesData.listContent[it]
                            )
                        )
                    }
                }
                _tempNotes.postValue(temp)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTempNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            val tempListOfIsAListItem = mutableListOf<Boolean>()
            val tempListOfIsChecked = mutableListOf<Boolean>()
            val tempListOfContent = mutableListOf<String>()
            tempNotes.value!!.size.let {
                for (i in 0..it) {
                    tempListOfContent.add(tempNotes.value!![i].content)
                    tempListOfIsAListItem.add(tempNotes.value!![i].isAListItem)
                    tempListOfIsChecked.add(tempNotes.value!![i].listItemIsChecked)
                }
            }
            currentNotesData.apply {
                notes = tempNotes.value!!
                listContent = tempListOfContent
                listIsAListItem = tempListOfIsAListItem
                listListItemIsChecked = tempListOfIsChecked
                isFavourite = this@NotesDetailFragment.isFavourite
                title = this@NotesDetailFragment.title
                dateFormatted = System.currentTimeMillis()
            }
            if (fromFab) {
                notesViewModel.insert(currentNotesData)
                fromFab = false
            } else {
                notesViewModel.update(currentNotesData)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_top, menu)
        //super.onCreateOptionsMenu(menu, inflater)
    }
}