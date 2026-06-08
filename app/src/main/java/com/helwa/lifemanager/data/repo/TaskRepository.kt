package com.helwa.lifemanager.data.repo

import android.content.Context
import com.helwa.lifemanager.data.dao.TaskDao
import com.helwa.lifemanager.data.db.AppDatabase
import com.helwa.lifemanager.data.entity.Task
import kotlinx.coroutines.flow.Flow

/** الواجهة الوحيدة للتعامل مع بيانات المهام (Repository Pattern) */
class TaskRepository(private val dao: TaskDao) {

    val allTasks: Flow<List<Task>> = dao.observeAll()

    fun upcoming(now: Long = System.currentTimeMillis()): Flow<List<Task>> =
        dao.observeUpcoming(now)

    suspend fun getById(id: Long): Task? = dao.getById(id)

    suspend fun insert(task: Task): Long = dao.insert(task)

    suspend fun update(task: Task) = dao.update(task)

    suspend fun delete(task: Task) = dao.delete(task)

    suspend fun setCompleted(id: Long, completed: Boolean) = dao.setCompleted(id, completed)

    suspend fun updateReminderTime(id: Long, time: Long) = dao.updateReminderTime(id, time)

    companion object {
        @Volatile
        private var INSTANCE: TaskRepository? = null

        fun get(context: Context): TaskRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskRepository(AppDatabase.get(context).taskDao()).also { INSTANCE = it }
            }
    }
}
