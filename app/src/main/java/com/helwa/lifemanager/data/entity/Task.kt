package com.helwa.lifemanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * مهمة واحدة في التطبيق.
 *
 * [reminderTime] هو وقت التذكير بالميلي ثانية (epoch). لو يساوي 0 يبقى المهمة من غير تذكير.
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val description: String? = null,

    val category: Category = Category.PERSONAL,
    val priority: Priority = Priority.MEDIUM,
    val repeatType: RepeatType = RepeatType.ONCE,

    /** وقت التذكير بالميلي ثانية. 0 = بدون تذكير */
    val reminderTime: Long = 0L,

    val isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
) {
    val hasReminder: Boolean get() = reminderTime > 0L
}
