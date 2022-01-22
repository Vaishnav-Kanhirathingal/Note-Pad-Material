package com.kenetic.materialpad.notepad.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.taskpad.dataclass.TasksData

@Database(
    entities = [
        NotesData::class,
        //TasksData::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(NotesTypeConverter::class)
abstract class NotesDataBase : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDataBase? = null
        fun getDatabase(context: Context): NotesDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDataBase::class.java,
                    "notes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}