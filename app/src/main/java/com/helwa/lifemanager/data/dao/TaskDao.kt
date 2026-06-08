package com.helwa.lifemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.helwa.lifemanager.data.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, reminderTime ASC, createdAt DESC")
    fun observeAll(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): Task?

    /** المهام اللي ليها تذكير قادم (في المستقبل وغير مكتملة) مرتبة بالأقرب */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND reminderTime > :now ORDER BY reminderTime ASC")
    fun observeUpcoming(now: Long): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("UPDATE tasks SET reminderTime = :time WHERE id = :id")
    suspend fun updateReminderTime(id: Long, time: Long)
}
