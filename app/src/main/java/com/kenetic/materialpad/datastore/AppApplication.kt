package com.kenetic.materialpad.datastore

import android.app.Application
import com.kenetic.materialpad.notepad.data.NotesDataBase
import com.kenetic.materialpad.taskpad.data.TasksDataBase

class AppApplication : Application() {
    val notesDatabase: NotesDataBase by lazy { NotesDataBase.getDatabase(this) }
    val taskDatabase: TasksDataBase by lazy { TasksDataBase.getDatabase(this) }
}