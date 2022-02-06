package com.kenetic.materialpad.taskpad.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kenetic.materialpad.R
import com.kenetic.materialpad.databinding.FragmentTasksDetailBinding
import com.kenetic.materialpad.databinding.PromptConfirmationBinding
import com.kenetic.materialpad.databinding.PromptTitleBinding
import com.kenetic.materialpad.datastore.AppApplication
import com.kenetic.materialpad.taskpad.adapters.TasksDetailScreenAdapter
import com.kenetic.materialpad.taskpad.dataclass.Task
import com.kenetic.materialpad.taskpad.dataclass.TasksData
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModel
import com.kenetic.materialpad.taskpad.viewmodel.TasksViewModelFactory

private const val TAG = "TasksDetailFragment"

class TasksDetailFragment : Fragment() {
    private var fileChanged = false
    private var taskId = 0
    private var unSaved = true
    private lateinit var adapter: TasksDetailScreenAdapter
    private lateinit var binding: FragmentTasksDetailBinding
    private val taskViewModel: TasksViewModel by activityViewModels {
        TasksViewModelFactory(
            (activity?.application as AppApplication).appGeneralDatabase.tasksDao()
        )
    }
    private lateinit var currentTasksData: TasksData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments.let {
            unSaved = it!!.getBoolean("from_fab")
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
        taskViewModel.tempTaskList.observe(viewLifecycleOwner) {
            fileChanged = true
            adapter.submitList(it)
        }
        binding.apply {
            tasksDetailRecycler.layoutManager = GridLayoutManager(requireContext(), 1)
            tasksDetailRecycler.adapter = adapter
            setBottomControls()
            setSideControls()
        }
    }

    private fun setBottomControls() {
        binding.apply {
            cleanLayout.setOnClickListener {
                taskViewModel.cleanListOfTasks(requireContext())
            }
            shareLayout.setOnClickListener {
                taskViewModel.share(currentTasksData.hasAReminder, currentTasksData.reminder)
            }
            favouriteLayout.setOnClickListener {
                currentTasksData.isFavourite = !currentTasksData.isFavourite
                setFavouriteIcon()
            }
            reminderLayout.setOnClickListener {
                changeReminder()
                Log.i(TAG, "reminder listener working")
            }
        }
    }

    private fun setSideControls() {
        binding.apply {
            sideControlsSave.setOnClickListener {//---------------------------------------------save
                Log.i(TAG, "save side menu button working properly")
                saveTempTask()
            }
            val promptBinding = PromptConfirmationBinding.inflate(layoutInflater)
            val alertDialog = Dialog(requireContext())
            alertDialog.apply {
                setContentView(promptBinding.root)
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setCancelable(true)
            }//--------------------------------------------------------------generate-prompt-binding
            sideControlsRestore.setOnClickListener {//---------------------------------------restore
                alertDialog.apply {
                    promptBinding.apply {
                        questionTextView.setText(R.string.restore_task_list_question)
                        confirmationButton.setOnClickListener {
                            resetTempTaskData()
                            dismiss()
                        }
                        cancelButton.setOnClickListener {
                            dismiss()
                        }
                    }
                    show()
                }
                Log.i(TAG, "restore side menu button working properly")
            }
            sideControlsDelete.setOnClickListener {//-----------------------------------------delete
                alertDialog.apply {
                    promptBinding.apply {
                        questionTextView.setText(R.string.delete_task_list_question)
                        confirmationButton.setOnClickListener {
                            if (!unSaved) {
                                taskViewModel.delete(currentTasksData)
                            }
                            taskViewModel.resetTempTasks()
                            findNavController().navigate(
                                TasksDetailFragmentDirections.actionTasksDetailFragmentToTasksListFragment()
                            )
                            dismiss()
                        }
                        cancelButton.setOnClickListener {
                            dismiss()
                        }
                    }
                    show()
                }
                Log.i(TAG, "delete side menu button working properly")
            }
        }
    }

    private fun resetTempTaskData() {
        if (unSaved) {
            taskViewModel.setTempTasks(mutableListOf(Task(isDone = false, task = "")))
            currentTasksData = TasksData(
                listOfTasks = taskViewModel.tempTaskList.value!!,
                isFavourite = false,
                title = "Untitled",
                hasAReminder = false,
                dateFormatted = System.currentTimeMillis(),
                reminder = 0
            )
        } else {
            currentTasksData = taskViewModel.getById(taskId).asLiveData().value!!
            taskViewModel.setTempTasks(currentTasksData.listOfTasks.toMutableList())
        }
    }

    private fun saveTempTask() {
        currentTasksData.apply {
            listOfTasks = taskViewModel.tempTaskList.value!!.toList()
            dateFormatted = System.currentTimeMillis()
        }
        if (unSaved) {
            taskViewModel.insert(currentTasksData)
        } else {
            taskViewModel.update(currentTasksData)
        }
        fileChanged = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_top, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.title_change -> {
                Log.i(TAG, "prompt title changer layout")
                val promptBinding = PromptTitleBinding.inflate(layoutInflater)
                val alertDialog = Dialog(requireContext())
                alertDialog.apply {
                    setContentView(promptBinding.root)
                    window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setCancelable(true)
                    promptBinding.apply {
                        titleEditText.setText(currentTasksData.title)
                        cancelButton.setOnClickListener { alertDialog.dismiss() }
                        changeButton.setOnClickListener {
                            currentTasksData.title = titleEditText.text.toString()
                            alertDialog.dismiss()
                        }
                    }
                    show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeReminder() {
        currentTasksData.hasAReminder.apply {
            if (this) {
                currentTasksData.hasAReminder = false
                setAlarmIcon()
            } else {
                // TODO: alarm prompt
            }
        }
    }

    private fun setFavouriteIcon() {
        binding.favouriteImage.setImageResource(
            if (currentTasksData.isFavourite) {
                R.drawable.star_selected_24
            } else {
                R.drawable.star_unselected_24
            }
        )
    }

    private fun setAlarmIcon() {
        binding.reminderIcon.setImageResource(
            if (currentTasksData.hasAReminder) {
                R.drawable.alarm_on_24
            } else {
                R.drawable.alarm_off_24
            }
        )
    }
}