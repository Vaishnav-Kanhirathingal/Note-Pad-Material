package com.kenetic.materialpad.taskpad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kenetic.materialpad.databinding.FragmentTasksListBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.taskpad.adapters.TasksMainScreenAdapter
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModel
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModelFactory

private const val TAG = "TasksListFragment"

class TasksListFragment : Fragment() {
    private val taskViewModel: TasksViewModel by activityViewModels {
        TasksViewModelFactory(
            (activity?.application as AppApplication).appGeneralDatabase.tasksDao()
        )
    }
    private lateinit var binding: FragmentTasksListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigateFab.setOnClickListener {
            findNavController()
                .navigate(
                    TasksListFragmentDirections
                        .actionTasksListFragmentToNotesListFragment()
                )
        }
        binding.addTasksFab.setOnClickListener {
            findNavController().navigate(
                TasksListFragmentDirections
                    .actionTasksListFragmentToTasksDetailFragment(fromFab = true, taskId = 0)
            )
        }
        binding.tasksRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = TasksMainScreenAdapter(taskViewModel)
        taskViewModel.getAllId().asLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        binding.tasksRecyclerView.adapter = adapter
    }
}