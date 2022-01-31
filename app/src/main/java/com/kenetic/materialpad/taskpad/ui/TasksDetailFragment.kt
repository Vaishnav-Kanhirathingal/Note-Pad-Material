package com.kenetic.materialpad.taskpad.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentTasksDetailBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.taskpad.adapters.TasksDetailScreenAdapter
import com.kenetic.materialpad.taskpad.dataclass.Task
import com.kenetic.materialpad.taskpad.dataclass.TasksData
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModel
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

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
    private var _tempTaskList: MutableLiveData<MutableList<Task>> = MutableLiveData(mutableListOf())
    private val tempTaskList: MutableLiveData<MutableList<Task>> = _tempTaskList
    private lateinit var currentTasksData: TasksData
    private var taskHasReminder = false
    private var taskTitle = "Untitled"
    private var isTaskFavourite = false
    private var taskReminder: Long = 0

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
        resetTempTaskData()
        tempTaskList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        binding.apply {
            tasksDetailRecycler.layoutManager = GridLayoutManager(requireContext(), 1)
            tasksDetailRecycler.adapter = adapter
            cleanLayout.setOnClickListener { cleanTasks() }
            shareLayout.setOnClickListener { share() }
            favouriteLayout.setOnClickListener {
                isTaskFavourite = !isTaskFavourite
                setFavouriteIcon()
            }
            setReminder.setOnClickListener {
                changeReminder()
            }
        }
    }

    private fun resetTempTaskData() {
        _tempTaskList.value = if (fromFab) {
            mutableListOf(Task(isDone = false, task = ""))
        } else {
            taskViewModel.getById(taskId).asLiveData().value!!.listOfTasks.toMutableList()
        }
    }

    private fun saveTempTask() {
        currentTasksData = TasksData(
            listOfTasks = tempTaskList.value!!.toList(),
            isFavourite = isTaskFavourite,
            title = taskTitle,
            hasAReminder = taskHasReminder,
            dateFormatted = System.currentTimeMillis(),
            reminder = taskReminder
        )
        if (fromFab) {
            taskViewModel.insert(currentTasksData)
        } else {
            taskViewModel.update(currentTasksData)
        }
        fromFab = false
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
                resetTempTaskData()
                true
            }
            R.id.details_save -> {
                saveTempTask()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeReminder() {
        if (taskHasReminder) {
            taskHasReminder = false
            setAlarmIcon()
        } else {
            //todo - get reminder from user
        }
    }

    private fun cleanTasks() {
        val offset = 0
        tempTaskList.value!!.size.let {
            val cleaningList = tempTaskList.value!!
            for (i in 0..it) {
                if (cleaningList[i].isDone) {
                    _tempTaskList.value!!.removeAt(i - offset)
                    offset + 1
                }
            }
        }
        Toast.makeText(requireContext(), "Tasks removed - $offset", Toast.LENGTH_LONG).show()
    }

    private fun share() {
        CoroutineScope(Dispatchers.IO).launch {
            var sharableString = if (taskHasReminder) {
                "task been scheduled for - ${
                    SimpleDateFormat("HH:mm on dd:MM:yy").format(taskReminder)
                }"
            } else {
                ""
            }
            tempTaskList.value!!.size.let {
                for (i in 0..it) {
                    val tempOne = tempTaskList.value!![i]
                    sharableString+=
                        "${
                            if (tempOne.isDone) {
                                "[#] - "
                            } else {
                                "[ ] - "
                            }
                        }${tempOne.task}\n"
                }
            }
            Log.i(TAG, "message copied -\n${sharableString}")
            //todo - start intent for sharing
        }
    }

    private fun setFavouriteIcon() {
        binding.favouriteImage.setImageResource(
            if (isTaskFavourite) {
                R.drawable.star_selected_24
            } else {
                R.drawable.star_unselected_24
            }
        )
    }

    private fun setAlarmIcon() {
        binding.reminderIcon.setImageResource(
            if (taskHasReminder) {
                R.drawable.alarm_on_24
            } else {
                R.drawable.alarm_off_24
            }
        )
    }
}