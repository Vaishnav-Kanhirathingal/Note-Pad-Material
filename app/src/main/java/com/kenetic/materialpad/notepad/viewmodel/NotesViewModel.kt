package com.kenetic.materialpad.notepad.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kenetic.materialpad.notepad.data.NotesDao
import com.kenetic.materialpad.notepad.dataclass.Note
import com.kenetic.materialpad.notepad.dataclass.NotesData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

private const val TAG = "NotesViewModel"

class NotesViewModel(private val notesDao: NotesDao) : ViewModel() {
    private var _tempNotes: MutableLiveData<MutableList<Note>> = MutableLiveData(mutableListOf())
    val tempNotes: MutableLiveData<MutableList<Note>> get() = _tempNotes
    var activeElement = 0

    //-----------------------------------------------------------------------------tempNotes-changer
    fun setTempNotes(temp: MutableList<Note>) {
        _tempNotes.postValue(temp)
        Log.i(TAG, "value posted")
    }

    fun textUpdater(position: Int, text: String) {
        _tempNotes.value!![position].content = text
        Log.i(TAG, "text updated to - $text")
    }

    fun changeCheckedStatus(position: Int, isChecked: Boolean) {
        _tempNotes.value!![position].listItemIsChecked = isChecked
        Log.i(TAG, "changed isChecked status for $position to $isChecked")
    }

    //---------------------------------------------------------------------------set-active-position

    fun setActive(position: Int) {
        activeElement = position
    }

    //-------------------------------------------------------------------------------bottom-controls
    fun changeIsListItemAtTempNotes() {
        _tempNotes.value!![activeElement].isAListItem =
            !tempNotes.value!![activeElement].isAListItem
        Log.i(TAG, "isAListItem changed for element - $activeElement")
    }

    fun addTabAtTempNotes() {
        _tempNotes.value!![activeElement].content.plus("\t")
        Log.i(TAG, "tab space added to element - $activeElement")
    }

    fun shareNotes(title: String, isFavourite: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            var sharableText = "\ndate last edited - ${
                SimpleDateFormat("HH:mm dd:MM:yy").format(System.currentTimeMillis())
            }\nTitle [${
                if (isFavourite) {
                    "💖"
                } else {
                    " "
                }
            }] - $title"
            tempNotes.value!!.size.let {
                for (i in 0 until it) {
                    val tempOne = tempNotes.value!![i]
                    sharableText += "\n${
                        if (tempOne.isAListItem) {
                            if (tempOne.listItemIsChecked) {
                                "[#] - "
                            } else {
                                "[ ] - "
                            }
                        } else {
                            ""
                        }
                    }${tempOne.content}"
                }
            }
            Log.i(TAG, "\n$sharableText")
            // TODO: start intent for sharing
        }
    }

    //----------------------------------------------------------------------------------key-detector
    fun onEnterKey(position: Int, text: String) {
        val topNote = tempNotes.value!![position]
        // TODO: -------------------------------------outOfIndex-Error-in-above-line-has-to-be-fixed
        Log.i(
            TAG,
            "index for tempNotes = $position and size of tempNotes - ${tempNotes.value!!.size}"
        )
        _tempNotes.value!!.add(
            position + 1,
            Note(
                content = text,
                isAListItem = topNote.isAListItem,
                listItemIsChecked = topNote.listItemIsChecked
            )
        )
        activeElement = position + 1
    }

    fun onBackSpaceKey(position: Int, carryText: String) {
        if (tempNotes.value!![position].isAListItem) {
            _tempNotes.value!![position].isAListItem = false
        } else if (position > 0) {
            _tempNotes.value!!.removeAt(position)
            _tempNotes.value!![position - 1].content += carryText
        }
        activeElement = position - 1
    }

    //-----------------------------------------------------------------------------------model-reset
    fun resetModel() {
        _tempNotes = MutableLiveData(mutableListOf())
        activeElement = 0
    }

    //---------------------------------------------------------------------------------Dao-functions
    fun insert(nd: NotesData) {
        CoroutineScope(Dispatchers.IO).launch { notesDao.insert(nd) }
    }

    fun update(nd: NotesData) {
        CoroutineScope(Dispatchers.IO).launch { notesDao.update(nd) }
    }

    fun delete(nd: NotesData) {
        CoroutineScope(Dispatchers.IO).launch { notesDao.delete(nd) }
    }

    fun getAllId(): Flow<List<Int>> = notesDao.getAllId()
    fun getById(id: Int): Flow<NotesData> = notesDao.getById(id)
    //-------------------------------------------------------------------------------close-viewModel
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