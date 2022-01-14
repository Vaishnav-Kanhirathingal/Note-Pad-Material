package com.kenetic.materialpad.taskpad.data

import androidx.room.*
import com.kenetic.materialpad.taskpad.dataclass.TasksData
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskData:TasksData)

    @Update
    suspend fun update(taskData: TasksData)

    @Delete
    suspend fun delete(taskData: TasksData)

    @Query("SELECT * FROM task_data")
    fun getAll():Flow<List<TasksData>>

    @Query("SELECT id FROM task_data")
    fun getAllId():Flow<List<Int>>

    @Query("SELECT * FROM task_data where id = :id")
    fun getById(id:Int):Flow<TasksData>
}