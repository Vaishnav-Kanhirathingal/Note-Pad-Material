package com.kenetic.materialpad.taskpad.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_data")
data class TasksData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "list_of_tasks") var listOfTasks: List<Task>,
    //adding new variables--------------------------------------------------------------------------
    @ColumnInfo(name = "list_is_done") var listIsDone: List<Boolean>,
    @ColumnInfo(name = "list_task") var listTask: List<String>,
    //adding new variables--------------------------------------------------------------------------
    @ColumnInfo(name = "is_favourite") var isFavourite: Boolean,
    @ColumnInfo(name = "title") var title: String,
    //@ColumnInfo(name = "is_protected") var isProtected :Boolean,
    @ColumnInfo(name = "has_a_reminder") var hasAReminder: Boolean
    //todo - reminder, date
)