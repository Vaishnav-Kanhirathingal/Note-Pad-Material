package com.kenetic.materialpad.taskpad.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kenetic.materialpad.taskpad.dataclass.Task
import com.kenetic.materialpad.taskpad.dataclass.TasksData

@ProvidedTypeConverter
class TasksTypeConverter {
    //----------------------------------------------------------------------------------------------
    @TypeConverter
    fun fromListOfBoolean(temp: List<Boolean>): String =
        Gson().toJson(temp)//todo - check for consistency

    @TypeConverter
    fun toListOfBoolean(temp: String): List<Boolean> =
        Gson().fromJson(temp, listOf<Boolean>().javaClass)

    @TypeConverter
    fun fromListOfString(temp: List<String>): String =
        Gson().toJson(temp)//todo - check for consistency

    @TypeConverter
    fun toListOfString(temp: String): List<String> =
        Gson().fromJson(temp, listOf<String>().javaClass)

    //----------------------------------------------------------------------------------------------
    @TypeConverter
    fun fromListOfTask(temp: List<Task>): String =
        Gson().toJson(temp)//todo - check for consistency

    @TypeConverter
    fun toListOfTask(temp: String): List<Task> =
        Gson().fromJson(temp, listOf<Task>().javaClass)
}