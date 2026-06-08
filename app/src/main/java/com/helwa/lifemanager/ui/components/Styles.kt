package com.helwa.lifemanager.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.helwa.lifemanager.data.entity.Category
import com.helwa.lifemanager.data.entity.Priority
import com.helwa.lifemanager.ui.theme.CatHealth
import com.helwa.lifemanager.ui.theme.CatOther
import com.helwa.lifemanager.ui.theme.CatPersonal
import com.helwa.lifemanager.ui.theme.CatShopping
import com.helwa.lifemanager.ui.theme.CatWork
import com.helwa.lifemanager.ui.theme.PriorityHigh
import com.helwa.lifemanager.ui.theme.PriorityLow
import com.helwa.lifemanager.ui.theme.PriorityMedium

fun Category.color(): Color = when (this) {
    Category.PERSONAL -> CatPersonal
    Category.WORK -> CatWork
    Category.HEALTH -> CatHealth
    Category.SHOPPING -> CatShopping
    Category.OTHER -> CatOther
}

fun Category.icon(): ImageVector = when (this) {
    Category.PERSONAL -> Icons.Filled.Person
    Category.WORK -> Icons.Filled.Work
    Category.HEALTH -> Icons.Filled.FavoriteBorder
    Category.SHOPPING -> Icons.Filled.ShoppingCart
    Category.OTHER -> Icons.Filled.MoreHoriz
}

fun Priority.color(): Color = when (this) {
    Priority.HIGH -> PriorityHigh
    Priority.MEDIUM -> PriorityMedium
    Priority.LOW -> PriorityLow
}
