package com.kenetic.materialpad.taskpad.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentTasksDetailBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.taskpad.adapters.TasksDetailScreenAdapter
import com.kenetic.materialpad.taskpad.dataclass.Task
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModel
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModelFactory

private const val TAG = "TasksDetailFragment"

class TasksDetailFragment : Fragment() {
    private var taskId = 0
    private var fromFab = true
    private lateinit var adapter: TasksDetailScreenAdapter
    private lateinit var binding: FragmentTasksDetailBinding
    private val taskViewModel: TasksViewModel by activityViewModels {
        TasksViewModelFactory(
            (activity?.application as AppApplication).appGeneralDatabase.tasksDao()
        )
    }
    private var tempTaskList: MutableList<Task> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments.let {
            fromFab = it!!.getBoolean("from_fab")
            taskId = it.getInt("task_id")
        }
        binding = FragmentTasksDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        adapter = TasksDetailScreenAdapter()
        binding.tasksDetailRecycler.layoutManager = GridLayoutManager(this.requireContext(), 1)
        binding.tasksDetailRecycler.adapter = adapter
        setTempTaskData()
        adapter.submitList(tempTaskList)
    }

    private fun setTempTaskData() {
        val temp: MutableList<Task> = arrayListOf()
        if (fromFab) {
            temp.add(Task(isDone = false, task = ""))
        } else {
            val tempTaskData = taskViewModel.getById(taskId).asLiveData().value!!
            tempTaskData.listIsDone.size.let {
                for (i in 0..it) {
                    temp.add(
                        i,//todo - check if it works
                        Task(
                            isDone = tempTaskData.listIsDone[i],
                            task = tempTaskData.listTask[i]
                        )
                    )
                }
            }
        }
        tempTaskList = temp
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_top, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.details_delete -> {
                //add delete prompt then delete this item using the id
                true
            }
            R.id.details_recover -> {
                tempTaskList = arrayListOf()
                setTempTaskData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}