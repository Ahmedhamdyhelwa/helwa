package com.helwa.lifemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.helwa.lifemanager.data.entity.Category
import com.helwa.lifemanager.data.entity.Priority
import com.helwa.lifemanager.data.entity.RepeatType
import com.helwa.lifemanager.data.entity.Task
import com.helwa.lifemanager.ui.components.DatePickerModal
import com.helwa.lifemanager.ui.components.TimePickerDialog
import com.helwa.lifemanager.ui.components.color
import com.helwa.lifemanager.ui.components.icon
import com.helwa.lifemanager.ui.viewmodel.TaskViewModel
import com.helwa.lifemanager.util.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: Long,
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val editing = taskId > 0L

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.PERSONAL) }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var repeat by remember { mutableStateOf(RepeatType.ONCE) }
    var reminderTime by remember { mutableLongStateOf(0L) }
    var loaded by remember { mutableStateOf(false) }

    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var pickedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(taskId) {
        if (editing && !loaded) {
            viewModel.allTasks.value.find { it.id == taskId }?.let { t ->
                title = t.title
                description = t.description ?: ""
                category = t.category
                priority = t.priority
                repeat = t.repeatType
                reminderTime = t.reminderTime
            }
            loaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editing) "تعديل المهمة" else "مهمة جديدة") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    if (editing) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "حذف",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("اسم المهمة") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("وصف (اختياري)") },
                shape = RoundedCornerShape(16.dp),
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel("الفئة")
            ChipFlow {
                Category.entries.forEach { c ->
                    FilterChip(
                        selected = category == c,
                        onClick = { category = c },
                        label = { Text(c.arabic) },
                        leadingIcon = {
                            Icon(c.icon(), null, modifier = Modifier.size(18.dp), tint = c.color())
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = c.color().copy(alpha = 0.18f)
                        )
                    )
                }
            }

            SectionLabel("الأولوية")
            ChipFlow {
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.arabic) },
                        leadingIcon = {
                            Box(
                                Modifier
                                    .size(12.dp)
                                    .padding(1.dp)
                            ) {
                                Box(
                                    Modifier
                                        .size(10.dp)
                                        .background(p.color(), RoundedCornerShape(50))
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = p.color().copy(alpha = 0.18f)
                        )
                    )
                }
            }

            SectionLabel("التكرار")
            ChipFlow {
                RepeatType.entries.forEach { r ->
                    FilterChip(
                        selected = repeat == r,
                        onClick = { repeat = r },
                        label = { Text(r.arabic) }
                    )
                }
            }

            SectionLabel("التذكير")
            ChipFlow {
                FilterChip(
                    selected = reminderTime == 0L,
                    onClick = { reminderTime = 0L },
                    label = { Text("بدون") }
                )
                FilterChip(
                    selected = false,
                    onClick = { reminderTime = DateUtils.afterMinutes(15) },
                    label = { Text("بعد ١٥ دقيقة") }
                )
                FilterChip(
                    selected = false,
                    onClick = { reminderTime = DateUtils.afterMinutes(60) },
                    label = { Text("بعد ساعة") }
                )
                FilterChip(
                    selected = reminderTime > 0L,
                    onClick = { showDate = true },
                    label = { Text("وقت محدد") },
                    leadingIcon = { Icon(Icons.Filled.Schedule, null, Modifier.size(18.dp)) }
                )
            }
            if (reminderTime > 0L) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "⏰ ${DateUtils.dateTime(reminderTime)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    val t = Task(
                        id = if (editing) taskId else 0L,
                        title = title.trim(),
                        description = description.trim().ifBlank { null },
                        category = category,
                        priority = priority,
                        repeatType = repeat,
                        reminderTime = reminderTime
                    )
                    scope.launch { viewModel.addOrUpdate(t) }
                    onBack()
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(if (editing) "حفظ التعديلات" else "إضافة المهمة", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    // اختيار التاريخ ثم الوقت
    if (showDate) {
        val dateState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = if (reminderTime > 0) reminderTime else System.currentTimeMillis()
        )
        DatePickerModal(
            datePickerState = dateState,
            onConfirm = {
                pickedDateMillis = dateState.selectedDateMillis ?: System.currentTimeMillis()
                showDate = false
                showTime = true
            },
            onDismiss = { showDate = false }
        )
    }
    if (showTime) {
        val cal = remember { Calendar.getInstance() }
        val timeState = androidx.compose.material3.rememberTimePickerState(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            is24Hour = false
        )
        TimePickerDialog(
            state = timeState,
            onConfirm = {
                val c = Calendar.getInstance().apply {
                    timeInMillis = pickedDateMillis
                    set(Calendar.HOUR_OF_DAY, timeState.hour)
                    set(Calendar.MINUTE, timeState.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                reminderTime = c.timeInMillis
                showTime = false
            },
            onDismiss = { showTime = false }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف المهمة") },
            text = { Text("متأكد إنك عايز تحذف \"$title\"؟") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.allTasks.value.find { it.id == taskId }?.let { viewModel.delete(it) }
                    showDeleteConfirm = false
                    onBack()
                }) { Text("حذف", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Spacer(Modifier.height(18.dp))
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}
