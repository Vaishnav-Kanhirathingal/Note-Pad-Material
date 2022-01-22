package com.kenetic.materialpad.datastore

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kenetic.materialpad.notepad.dataclass.Notes
import com.kenetic.materialpad.taskpad.dataclass.Task

class AppGeneralTypeConverter {
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
    fun fromListOfNotes(temp: List<Notes>): String =
        Gson().toJson(temp)//todo - check for consistency

    @TypeConverter
    fun toListOfNotes(temp: String): List<Notes> =
        Gson().fromJson(temp, listOf<Notes>().javaClass)

    //----------------------------------------------------------------------------------------------
    @TypeConverter
    fun fromListOfTasks(temp: List<Task>): String =
        Gson().toJson(temp)//todo - check for consistency

    @TypeConverter
    fun toListOfTasks(temp: String): List<Task> =
        Gson().fromJson(temp, listOf<Task>().javaClass)
    //----------------------------------------------------------------------------------------------
}