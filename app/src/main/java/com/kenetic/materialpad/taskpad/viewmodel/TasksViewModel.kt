package com.kenetic.materialpad.taskpad.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.taskpad.data.TasksDao
import com.kenetic.materialpad.taskpad.dataclass.TasksData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TasksViewModel(private val tasksDao: TasksDao):ViewModel() {

    fun insert(td: TasksData) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.insert(td)
        }
    }

    fun update(td: TasksData) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.update(td)
        }
    }

    fun delete(td: TasksData) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.delete(td)
        }
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