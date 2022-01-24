package com.kenetic.materialpad.notepad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kenetic.materialpad.databinding.FragmentNotesListBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.notepad.adapters.NotesMainScreenAdapter
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModel
import com.kenetic.materialpad.notepad.viewmodel.NotesViewModelFactory

private const val TAG = "NotesListFragment"

class NotesListFragment : Fragment() {
    private val notesViewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory(
            (activity?.application as AppApplication).appGeneralDatabase.notesDao()
        )
    }
    private lateinit var binding: FragmentNotesListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigateFab.setOnClickListener {
            findNavController().navigate(
                NotesListFragmentDirections
                    .actionNotesListFragmentToTasksListFragment()
            )
        }
        binding.addNotesFab.setOnClickListener {
            findNavController().navigate(
                NotesListFragmentDirections
                    .actionNotesListFragmentToNotesDetailFragment(
                        fromFab = true,
                        notesId = 0
                    )
            )
        }
        val adapter =
            NotesMainScreenAdapter(
                viewModel = notesViewModel,
                lifecycleOwner = viewLifecycleOwner,
                fragInstance = this
            )
        binding.notesMainRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.notesMainRecycler.adapter = adapter
        notesViewModel.getAllId().asLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}