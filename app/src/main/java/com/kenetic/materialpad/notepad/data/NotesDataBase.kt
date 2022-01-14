package com.kenetic.materialpad.notepad.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kenetic.materialpad.notepad.dataclass.NotesData

@Database(
    entities = [NotesData::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDataBase:RoomDatabase() {
    abstract fun notesDao():NotesDao

    companion object{
        @Volatile
        private var INSTANCE: NotesDataBase? = null
        fun getDatabase(context: Context): NotesDataBase {
            return INSTANCE ?: synchronized(this){
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