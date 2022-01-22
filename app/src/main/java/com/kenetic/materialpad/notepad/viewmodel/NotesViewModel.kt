package com.kenetic.materialpad.notepad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kenetic.materialpad.notepad.data.NotesDao
import com.kenetic.materialpad.notepad.dataclass.NotesData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotesViewModel(private val notesDao: NotesDao) : ViewModel() {

    fun insert(nd: NotesData) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDao.insert(nd)
        }
    }

    fun update(nd: NotesData) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDao.update(nd)
        }
    }

    fun delete(nd: NotesData) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDao.delete(nd)
        }
    }

    fun getAll(): Flow<List<NotesData>> = notesDao.getAll()

    fun getAllId(): Flow<List<Int>> = notesDao.getAllId()

    fun getById(id: Int): Flow<NotesData> = notesDao.getById(id)

}

class NotesViewModelFactory(private val notesDao: NotesDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(notesDao) as T
        }
        throw IllegalArgumentException("unknown model class")
    }
}