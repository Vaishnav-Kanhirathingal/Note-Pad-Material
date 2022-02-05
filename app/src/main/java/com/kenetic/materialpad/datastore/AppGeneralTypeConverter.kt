package com.kenetic.materialpad.datastore

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kenetic.materialpad.notepad.dataclass.Note
import com.kenetic.materialpad.taskpad.dataclass.Task

private const val TAG = "AppGeneralTypeConverter"

class AppGeneralTypeConverter {
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
    fun toListOfNotes(temp: String): List<Note> {//------------------------working-properly-verified
        val listOfPureNotes = mutableListOf<Note>()
        for (i in Gson().fromJson(temp, mutableListOf<String>().javaClass)) {
//            Log.i(TAG, "end\n\nloop element - $i")
            val tempNotesJsonAsList = Gson().fromJson(i, mutableListOf<String>().javaClass)
            //----------------------------------------------------------------getting-note-variables
            val isAListItem = Gson().fromJson(tempNotesJsonAsList[0], true.javaClass)
            val listItemIsChecked = Gson().fromJson(tempNotesJsonAsList[1], true.javaClass)
            val content = Gson().fromJson(tempNotesJsonAsList[2], "".javaClass)
            //-----------------------------------------------------------------adding-note-variables
            listOfPureNotes.add(
                Note(isAListItem, listItemIsChecked, content)
            )
        }
        //--------------------------------------checking-elements TODO: remove-this-in-final-version
//        for (i in listOfPureNotes) {
//            Log.i(
//                TAG,
//                "Note contents\n" +
//                        "[content] - [${i.content.javaClass}] - ${i.content}\n" +
//                        "[isAListItem] - [${i.isAListItem.javaClass}] - ${i.isAListItem}\n" +
//                        "[listItemIsChecked] - [${i.listItemIsChecked.javaClass}] - ${i.listItemIsChecked}"
//            )
//        }
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
        for (i in Gson().fromJson(temp, mutableListOf<String>().javaClass)) {
            val tempTasksJsonAsList = Gson().fromJson(i, mutableListOf<String>().javaClass)
            listOfPureTasks.add(
                Task(
                    task = Gson().fromJson(tempTasksJsonAsList[0], "".javaClass),
                    isDone = Gson().fromJson(tempTasksJsonAsList[1], true.javaClass)
                )
            )
        }
        return listOfPureTasks
    }
//----------------------------------------------------------------------------------------------
}