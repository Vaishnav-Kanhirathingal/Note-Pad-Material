package com.kenetic.materialpad.notepad.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentNotesListBinding

private const val TAG = "NotesListFragment"

class NotesListFragment : Fragment() {
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
        //todo - set the correct onclick listener
        binding.mainBottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_notes_fragment -> {
                    Toast
                        .makeText(requireContext(), "already in notes fragment", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                R.id.to_tasks_fragment -> {
                    findNavController()
                        .navigate(
                            NotesListFragmentDirections
                                .actionNotesListFragmentToTasksListFragment()
                        )
                    true
                }
                else -> {
                    Toast
                        .makeText(requireContext(), "bottom nav error", Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            }
        }
        binding.addNotesFab.setOnClickListener {
            findNavController().navigate(
                NotesListFragmentDirections
                    .actionNotesListFragmentToNotesDetailFragment()
            )
        }
        //todo - set adapter
    }
}