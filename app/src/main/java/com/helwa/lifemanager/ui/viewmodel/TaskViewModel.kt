package com.helwa.lifemanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.helwa.lifemanager.data.entity.Category
import com.helwa.lifemanager.data.entity.RepeatType
import com.helwa.lifemanager.data.entity.Task
import com.helwa.lifemanager.data.repo.TaskRepository
import com.helwa.lifemanager.notification.ReminderScheduler
import com.helwa.lifemanager.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** حالة لوحة اليوم */
data class DashboardState(
    val todayTasks: List<Task> = emptyList(),
    val completedCount: Int = 0,
    val remainingCount: Int = 0,
    val nextReminder: Task? = null
)

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = TaskRepository.get(app)

    val allTasks: StateFlow<List<Task>> = repo.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboard: StateFlow<DashboardState> = repo.allTasks
        .map { tasks ->
            val today = tasks.filter {
                it.repeatType == RepeatType.DAILY || DateUtils.isToday(it.reminderTime) ||
                    (it.reminderTime == 0L && DateUtils.isToday(it.createdAt))
            }
            val next = tasks
                .filter { !it.isCompleted && it.hasReminder && it.reminderTime > System.currentTimeMillis() }
                .minByOrNull { it.reminderTime }
            DashboardState(
                todayTasks = today,
                completedCount = today.count { it.isCompleted },
                remainingCount = today.count { !it.isCompleted },
                nextReminder = next
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    fun addOrUpdate(task: Task) {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            if (task.id == 0L) {
                val newId = repo.insert(task)
                val saved = task.copy(id = newId)
                if (saved.hasReminder && !saved.isCompleted) ReminderScheduler.schedule(ctx, saved)
            } else {
                repo.update(task)
                ReminderScheduler.cancel(ctx, task.id)
                if (task.hasReminder && !task.isCompleted) ReminderScheduler.schedule(ctx, task)
            }
        }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            val newValue = !task.isCompleted
            repo.setCompleted(task.id, newValue)
            if (newValue) {
                ReminderScheduler.cancel(ctx, task.id)
            } else if (task.hasReminder && task.reminderTime > System.currentTimeMillis()) {
                ReminderScheduler.schedule(ctx, task.copy(isCompleted = false))
            }
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), task.id)
            repo.delete(task)
        }
    }

    fun filterByCategory(tasks: List<Task>, category: Category?): List<Task> =
        if (category == null) tasks else tasks.filter { it.category == category }
}
