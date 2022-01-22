package com.kenetic.materialpad.taskpad.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentTasksListBinding
import com.kenetic.materialpad.notepad.ui.NotesListFragmentDirections

private const val TAG = "TasksListFragment"

class TasksListFragment : Fragment() {
    private lateinit var binding: FragmentTasksListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainBottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_notes_fragment -> {
                    findNavController()
                        .navigate(
                            TasksListFragmentDirections
                                .actionTasksListFragmentToNotesListFragment()
                        )
                    true
                }
                R.id.to_tasks_fragment -> {
                    Toast
                        .makeText(requireContext(), "already in notes fragment", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                else ->{
                    Toast
                        .makeText(requireContext(), "bottom nav error", Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            }
        }
        binding.addTasksFab.setOnClickListener {
            findNavController().navigate(
                TasksListFragmentDirections
                    .actionTasksListFragmentToTasksDetailFragment(fromFab = true, taskId = 0)
            )
        }
    }
}