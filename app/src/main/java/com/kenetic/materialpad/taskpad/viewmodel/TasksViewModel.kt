package com.kenetic.materialpad.taskpad.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kenetic.materialpad.taskpad.data.TasksDao
import com.kenetic.materialpad.taskpad.dataclass.Task
import com.kenetic.materialpad.taskpad.dataclass.TasksData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

private const val TAG = "TasksViewModel"

class TasksViewModel(private val tasksDao: TasksDao) : ViewModel() {
    private var _tempTaskList: MutableLiveData<MutableList<Task>> = MutableLiveData(mutableListOf())
    val tempTaskList: MutableLiveData<MutableList<Task>> = _tempTaskList

    fun setTempTasks(temp: MutableList<Task>) {
        _tempTaskList.value = temp
    }

    //-------------------------------------------------------------------------------bottom-controls
    fun cleanListOfTasks(context: Context) {
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
        Toast.makeText(context, "Tasks removed - $offset", Toast.LENGTH_LONG).show()
    }

    fun share(taskHasReminder: Boolean, taskReminder: Long) {
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
                    sharableString +=
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

    //----------------------------------------------------------------------------------key-detector
    fun onBackSpaceKey() {
    }

    fun onEnterKey() {
    }

    //-------------------------------------------------------------------------------reset-viewModel
    fun resetTempTasks() {
    }

    //---------------------------------------------------------------------------------Dao-functions
    fun insert(td: TasksData) {
        CoroutineScope(Dispatchers.IO).launch { tasksDao.insert(td) }
    }

    fun update(td: TasksData) {
        CoroutineScope(Dispatchers.IO).launch { tasksDao.update(td) }
    }

    fun delete(td: TasksData) {
        CoroutineScope(Dispatchers.IO).launch { tasksDao.delete(td) }
    }

    fun getAll(): Flow<List<TasksData>> = tasksDao.getAll()
    fun getAllId(): Flow<List<Int>> = tasksDao.getAllId()
    fun getById(id: Int): Flow<TasksData> = tasksDao.getById(id)
}

class TasksViewModelFactory(private val tasksDao: TasksDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(tasksDao) as T
        }
        throw IllegalArgumentException("unknown model class")
    }
}