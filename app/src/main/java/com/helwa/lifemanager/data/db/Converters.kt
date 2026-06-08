package com.helwa.lifemanager.data.db

import androidx.room.TypeConverter
import com.helwa.lifemanager.data.entity.Category
import com.helwa.lifemanager.data.entity.Priority
import com.helwa.lifemanager.data.entity.RepeatType

/** تحويل الـ enums من وإلى نصوص عشان تتخزن في Room */
class Converters {
    @TypeConverter
    fun priorityToString(value: Priority): String = value.name

    @TypeConverter
    fun stringToPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun repeatToString(value: RepeatType): String = value.name

    @TypeConverter
    fun stringToRepeat(value: String): RepeatType = RepeatType.valueOf(value)

    @TypeConverter
    fun categoryToString(value: Category): String = value.name

    @TypeConverter
    fun stringToCategory(value: String): Category = Category.valueOf(value)
}
