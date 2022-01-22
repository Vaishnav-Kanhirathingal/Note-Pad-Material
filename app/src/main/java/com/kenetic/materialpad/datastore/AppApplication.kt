package com.kenetic.materialpad.datastore

import android.app.Application

class AppApplication : Application() {
    //val notesDatabase: NotesDataBase by lazy { NotesDataBase.getDatabase(this) }
    //val taskDatabase: TasksDataBase by lazy { TasksDataBase.getDatabase(this) }
    val appGeneralDatabase: AppGeneralDatabase by lazy { AppGeneralDatabase.getDatabase(this) }
}