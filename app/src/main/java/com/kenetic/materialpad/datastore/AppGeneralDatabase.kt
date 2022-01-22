package com.kenetic.materialpad.datastore

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kenetic.materialpad.notepad.data.NotesDao
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.taskpad.data.TasksDao
import com.kenetic.materialpad.taskpad.dataclass.TasksData

@Database(
    entities = [
        NotesData::class,
        TasksData::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AppGeneralTypeConverter::class)
abstract class AppGeneralDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun tasksDao(): TasksDao

    companion object {
        @Volatile
        private var INSTANCE: AppGeneralDatabase? = null
        fun getDatabase(context: Context): AppGeneralDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppGeneralDatabase::class.java,
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