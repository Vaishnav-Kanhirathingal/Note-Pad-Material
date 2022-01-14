package com.kenetic.materialpad.notepad.data

import androidx.room.*
import com.kenetic.materialpad.notepad.dataclass.NotesData
import com.kenetic.materialpad.taskpad.dataclass.TasksData
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notesData: NotesData)

    @Update
    suspend fun update(notesData: NotesData)

    @Delete
    suspend fun delete(notesData: NotesData)

    @Query("SELECT * FROM notes_data")
    fun getAll(): Flow<List<NotesData>>

    @Query("SELECT id FROM notes_data")
    fun getAllId(): Flow<List<Int>>

    @Query("SELECT * FROM notes_data where id = :id")
    fun getById(id:Int): Flow<NotesData>
}