package com.kenetic.materialpad.taskpad.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentTasksDetailBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.taskpad.adapters.TasksDetailScreenAdapter
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModel
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModelFactory

private const val TAG = "TasksDetailFragment"

class TasksDetailFragment : Fragment() {
    private lateinit var binding: FragmentTasksDetailBinding
    private val taskViewModel: TasksViewModel by activityViewModels {
        TasksViewModelFactory(
            (activity?.application as AppApplication).taskDatabase.tasksDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        //binding.tasksDetailRecycler.adapter = TasksDetailScreenAdapter(taskViewModel)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_top, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}