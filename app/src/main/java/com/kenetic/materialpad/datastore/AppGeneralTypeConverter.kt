package com.kenetic.materialpad.datastore

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kenetic.materialpad.notepad.dataclass.Note
import com.kenetic.materialpad.taskpad.dataclass.Task

private const val TAG = "AppGeneralTypeConverter"

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
    fun fromListOfNotes(temp: List<Note>): String {
        val listOfNotesAsJson = mutableListOf<String>()
        for (i in temp) {
            val notesGson: MutableList<String> = mutableListOf()
            notesGson.add(Gson().toJson(i.isAListItem))
            notesGson.add(Gson().toJson(i.listItemIsChecked))
            notesGson.add(Gson().toJson(i.content))
            //-------------------------------------
            listOfNotesAsJson.add(Gson().toJson(notesGson))
        }
        val returnedValue = Gson().toJson(listOfNotesAsJson)
        Log.i(TAG, returnedValue)
        return returnedValue
    }

    @TypeConverter
    fun toListOfNotes(temp: String): List<Note> {
        val listOfPureNotes = mutableListOf<Note>()

        val returnType = object : TypeToken<MutableList<String>>() {}.javaClass

        for (i in Gson().fromJson(temp, mutableListOf<String>().javaClass)) {
            val tempNotesJsonAsList = Gson().fromJson(i, mutableListOf<String>().javaClass)
            listOfPureNotes.add(
                Note(
                    isAListItem = Gson().fromJson(tempNotesJsonAsList[0], true.javaClass),
                    listItemIsChecked = Gson().fromJson(tempNotesJsonAsList[1], true.javaClass),
                    content = Gson().fromJson(tempNotesJsonAsList[2], "".javaClass)
                )
            )
        }
        return listOfPureNotes
    }

    //----------------------------------------------------------------------------------------------
    @TypeConverter
    fun fromListOfTasks(temp: List<Task>): String {
        val listOfTasksAsJson = mutableListOf<String>()
        for (i in temp) {
            val tasksJson: MutableList<String> = mutableListOf()
            tasksJson.add(Gson().toJson(i.task))
            tasksJson.add(Gson().toJson(i.isDone))
            //-------------------------------------
            listOfTasksAsJson.add(Gson().toJson(tasksJson))
        }
        //--------------------------------------------------
        val returnedValue = Gson().toJson(listOfTasksAsJson)
        Log.i(TAG, returnedValue)
        return returnedValue
    }

    @TypeConverter
    fun toListOfTasks(temp: String): List<Task> {
        val listOfPureTasks = mutableListOf<Task>()
        //retrieves list of json string that is convertible to Task objects and iterates through it
        for (i in Gson().fromJson(temp, mutableListOf<String>().javaClass)) {
            //each string 'i' represents Task class object as a list of all its variables as json strings
            val tempTasksJsonAsList = Gson().fromJson(i, mutableListOf<String>().javaClass)
            listOfPureTasks.add(
                Task(
                    task = Gson().fromJson(tempTasksJsonAsList[0], "".javaClass),
                    isDone = Gson().fromJson(tempTasksJsonAsList[1], true.javaClass)
                )
            )
        }
        return Gson().fromJson(temp, listOf<Task>().javaClass)
    }
//----------------------------------------------------------------------------------------------
}