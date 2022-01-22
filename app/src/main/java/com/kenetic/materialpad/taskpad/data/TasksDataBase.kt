package com.kenetic.materialpad.taskpad.data

import android.content.Context
import androidx.room.*
import com.kenetic.materialpad.taskpad.dataclass.TasksData

@Database(
    entities = [TasksData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TasksTypeConverter::class)
abstract class TasksDataBase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao

    companion object {
        @Volatile
        private var INSTANCE: TasksDataBase? = null
        fun getDatabase(context: Context): TasksDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TasksDataBase::class.java,
                    "tasks_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}